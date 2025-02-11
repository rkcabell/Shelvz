package com.example.shelvz.ui.library

import BottomBar
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rajat.pdfviewer.compose.PdfRendererViewCompose

import com.example.shelvz.data.model.UserFile
import com.example.shelvz.util.DetailedCard
import com.example.shelvz.util.MediaSearchBar
import com.example.shelvz.util.extractPdfThumbnail
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isRefreshing = viewModel.isLoading.collectAsState(initial = false).value
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val selectedCard by viewModel.selectedCard.collectAsState()
    val selectedFile by viewModel.selectedFile.collectAsState()

    // Attempt at solving the refresh icon lingering
    LaunchedEffect(viewModel.isLoading.collectAsState().value) {
        if (!viewModel.isLoading.value) {
            Log.d("LibraryScreen", "Refreshing completed, hiding icon")
        }
    }

    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    snackbarMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage()
        }
    }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                viewModel.saveAndAddFile(context, uri)
            } ?: Log.e("LibraryScreen", "No file selected or selection canceled.")
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing,
            onRefresh = {
                Log.d("LibraryScreen", "Refreshing library...")
                scope.launch {
                    viewModel.refreshLibrary()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LibraryContent(
                viewModel = viewModel,
                launcher = filePickerLauncher
            )
        }
    }

    selectedCard?.let { file ->
        AnimatedSelectedCard(file = file, viewModel = viewModel)
    }

    // Display the selected file (PDF Viewer)
    selectedFile?.let { file ->
        PDFViewer(
            file = file,
            onClose = { viewModel.clearSelectedFile() }
        )
    }
}

@Composable
fun LibraryContent(
    viewModel: LibraryViewModel,
    launcher: ActivityResultLauncher<Array<String>>
) {
    val libraryItems by viewModel.fileList.collectAsState()
    val filters by viewModel.filters.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredItems = when (selectedFilter) {
        "All" -> libraryItems
        else -> libraryItems.filter { it.type.contains(selectedFilter, ignoreCase = true) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            filteredItems.isEmpty() -> OnEmptyLibrary(launcher)
            else -> LibraryFileList(
                filteredItems = filteredItems,
                filters = filters,
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                launcher = launcher,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun LibraryFileList(
    filteredItems: List<UserFile>,
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    launcher: ActivityResultLauncher<Array<String>>,
    viewModel: LibraryViewModel
) {
    val isDeleteMode by viewModel.isDeleteMode.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { LibraryAppBar() }
        item { RecentlyOpenedSection(viewModel = viewModel) }
        item { FilterRow(filters = filters, selectedFilter = selectedFilter, onFilterSelected) }
        item {
            LibraryGrid(
                filteredItems = filteredItems,
                viewModel = viewModel
            )
        }
    }

    if (!isDeleteMode) {
        UploadButton(launcher)
    }

    if (isDeleteMode) {
        DimmerOverlay(
            onDismiss = {
                Log.d("LibraryScreen", "Dimmer overlay dismissed")
                viewModel.setDeleteMode(false)
                viewModel.clearSelectedCard()
            })
        DeleteButtonOverlay(
            viewModel = viewModel,
            onDeleteConfirm = {
                viewModel.setDeleteMode(false)
                viewModel.clearSelectedCard()
            }
        )
    }
}

@Composable
fun OnEmptyLibrary(launcher: ActivityResultLauncher<Array<String>>) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { launcher.launch(arrayOf("*/*")) }) {
            Text(text = "Add a document to Library")
        }
    }
}

@Composable
fun FilterRow(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item {
            Icon(
                imageVector = Icons.Outlined.FilterList,
                contentDescription = "Filter Icon",
                modifier = Modifier.size(24.dp)
            )
        }
        filters.forEach { filter ->
            item {
                Button(
                    onClick = { onFilterSelected(filter) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedFilter == filter) Color.Blue else Color.Gray
                    )
                ) {
                    Text(text = filter, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun RecentlyOpenedSection(viewModel: LibraryViewModel) {
    val recentlyOpened by viewModel.loggedInUser
        .map { user ->
            Log.d("RecentlyOpenedDebug", "User Files: ${user?.recentlyOpenedFiles}")
            user?.recentlyOpenedFiles ?: emptyList()
        }
        .collectAsState(initial = emptyList())


    Text(
        text = "Recently Opened",
        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(bottom = 8.dp)
    )

    if (recentlyOpened.isNotEmpty()) {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val cardSize: Dp = (screenWidth - 32.dp) / 3


        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(recentlyOpened.size) { index ->
                val file = recentlyOpened[index]
                Card(
                    modifier = Modifier.size(cardSize, 60.dp).clickable {
                        // Handle file re-opening here
                        Log.d("RecentlyOpened", "File clicked: ${file.name}")
                        viewModel.selectFile(file)
                    },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = file.name, maxLines = 1)
                    }
                }
            }
        }
    } else {
        // Fallback text when the list is empty
        Text(
            text = "No recently opened files.",
            style = TextStyle(fontSize = 16.sp, fontStyle = FontStyle.Italic, color = Color.Gray),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun LibraryGrid(
    filteredItems: List<UserFile>,
    viewModel: LibraryViewModel
) {

    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardSize = viewModel.calculateCardSize(screenWidth)

    LaunchedEffect(filteredItems) {
        viewModel.logFilteredItems(filteredItems)
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp) // Restrict max height
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(this.maxHeight),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                filteredItems.size, key = { index -> filteredItems[index].id }
            ) { index ->
                val file = filteredItems[index]
                val thumbnailMap by viewModel.thumbnails.collectAsState()
                val thumbnail = thumbnailMap[file.id.toString()]

                // Load thumbnail when the file appears
                LaunchedEffect(file) {
                    viewModel.loadThumbnail(file, context)
                }

                DetailedCard(
                    title = file.name,
                    thumbnail = thumbnail,
                    modifier = Modifier
                        .size(cardSize, 120.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    Log.d("LibraryGrid", "Card clicked: ${file.name}")
                                    viewModel.selectFile(file)
                                    viewModel.markFileAsRecentlyOpened(file)
                                },
                                onLongPress = {
                                    Log.d("LibraryGrid", "Card long-pressed: ${file.name}")
                                    viewModel.setDeleteMode(true)
                                    viewModel.selectCard(file)
                                }
                            )
                        }
                )
            }
        }
    }
}


@Composable
fun UploadButton(launcher: ActivityResultLauncher<Array<String>>) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(onClick = { showDialog = true }) {
            Icon(imageVector = Icons.Default.Upload, contentDescription = "Upload")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Upload File") },
            text = { Text("Select a file to upload.") },
            confirmButton = {
                TextButton(onClick = {
                    launcher.launch(arrayOf("application/pdf"))
                    showDialog = false
                }) {
                    Text("Select File")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PDFViewer(file: UserFile, onClose: () -> Unit) {
    val context = LocalContext.current
    val localFile = File(context.filesDir, "user_files/${file.name}")

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f))) {
        if (localFile.exists()) {
            PdfRendererViewCompose(
                modifier = Modifier.fillMaxSize(),
                uri = Uri.fromFile(localFile)
            )
        } else {
            Text(
                text = "File not found.",
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }
        FloatingActionButton(
            onClick = { onClose() },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Text("Close")
        }
    }
}

@Composable
fun AnimatedSelectedCard(
    file: UserFile,
    viewModel: LibraryViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(2f), // Ensure it is above the dimmer
        contentAlignment = Alignment.Center
    ) {
        val scale = remember { Animatable(0f) }
        LaunchedEffect(file) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300)
            )
        }

        val thumbnailMap by viewModel.thumbnails.collectAsState()
        val thumbnail = thumbnailMap[file.id.toString()]
        val context = LocalContext.current

        // Load thumbnail if not already available
        LaunchedEffect(file) {
            viewModel.loadThumbnail(file, context)
        }

        DetailedCard(
            title = file.name,
            thumbnail = thumbnail,
            modifier = Modifier
                .size(250.dp)
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryAppBar(
    modifier: Modifier = Modifier,
) {
    var queryText by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    Row (
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures {
                    if (isExpanded) {
                        isExpanded = false // Close when tapping outside
                    }
                }
            }
    ) {
        MediaSearchBar(queryText = queryText,
            onQueryChange = { queryText = it },
            isExpanded = isExpanded,
            onExpandedChange = { isExpanded = it })
    }
}


@Composable
fun DimmerOverlay( onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .zIndex(1f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        Log.d("LibraryScreen", "Overlay clicked")
                        onDismiss()
                    },
                    onPress = {
                        // Allow gesture to propagate
                        tryAwaitRelease()
                    }
                )
            }
    )
}

@Composable
fun DeleteButtonOverlay(
    viewModel: LibraryViewModel,
    onDeleteConfirm: () -> Unit
) {
    val selectedCard by viewModel.selectedCard.collectAsState()

    if (selectedCard != null) { // Show overlay only if a card is selected
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
        ) {

//            AnimatedSelectedCard(file = selectedCard)

            DeleteButton(
                selectedCard = selectedCard,
                viewModel = viewModel,
                onDeleteConfirm = {
                    viewModel.clearSelectedCard()
                    viewModel.setDeleteMode(false)
                    onDeleteConfirm()
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}


@Composable
fun DeleteButton(
    selectedCard: UserFile?,
    viewModel: LibraryViewModel,
    onDeleteConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0f) }
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = tween(durationMillis = 300))
    }

    Box(
        modifier = modifier
            .padding(16.dp)
            .size(80.dp)
            .graphicsLayer(scaleX = scale.value, scaleY = scale.value)
            .background(Color.Red, shape = CircleShape)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                selectedCard?.let { file ->
                    viewModel.deleteFile(file)
                }
                onDeleteConfirm()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Delete",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium, // Adjust text style as needed
            maxLines = 1
        )
    }
}
