package com.example.shelvz.ui.library

import BottomBar
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rajat.pdfviewer.compose.PdfRendererViewCompose

import com.example.shelvz.data.model.UserFile
import com.example.shelvz.util.MediaSearchBar
import com.example.shelvz.util.MyResult
import com.example.shelvz.util.ShelvzTheme
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val refreshing = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                Log.d("LibraryScreen", "File picked: $uri")
                viewModel.saveAndAddFile(context, uri)
            } ?: Log.e("LibraryScreen", "No file selected or selection canceled.")
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = refreshing.value,
            onRefresh = {
                Log.d("LibraryScreen", "Refreshing library...")
                scope.launch {
                    refreshing.value = true
                    viewModel.refreshLibrary()
                    refreshing.value = false
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LibraryContent(
                viewModel = viewModel,
                launcher = filePickerLauncher,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@Composable
fun LibraryContent(
    viewModel: LibraryViewModel,
    launcher: ActivityResultLauncher<Array<String>>,
    snackbarHostState: SnackbarHostState
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
    var isDeleteMode by remember { mutableStateOf(false) }
    var selectedCard by remember { mutableStateOf<UserFile?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { LibraryAppBar() }
        item { RecentlyOpenedSection() }
        item { FilterRow(filters = filters, selectedFilter = selectedFilter, onFilterSelected) }
        item {
            LibraryGrid(
                filteredItems = filteredItems,
                viewModel = viewModel,
                onDeleteModeChange = { isDeleteMode = it },
                onCardSelected = { selectedCard = it }
            )
        }
    }

    if (!isDeleteMode) {
        UploadButton(launcher)
    }

    if (isDeleteMode) {
        DimmerOverlay(
            onDismiss = {
                Log.d("LibraryGrid", "Dimmer overlay dismissed")
                isDeleteMode = false
                selectedCard = null
            })
        DeleteButtonOverlay(
            selectedCard = selectedCard,
            viewModel = viewModel,
            onDeleteConfirm = {
                isDeleteMode = false
                selectedCard = null
            }
        )
    }

    selectedCard?.let { file ->
        AnimatedSelectedCard(file = file)
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
fun RecentlyOpenedSection() {
    val recentlyOpened = listOf("Book 1", "Book 2", "Book 3")
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardSize: Dp = (screenWidth - 32.dp) / 3
    Text(
        text = "Recently Opened",
        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(recentlyOpened.size) { index ->
            Card(
                modifier = Modifier.size(cardSize, 60.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = recentlyOpened[index])
                }
            }
        }
    }
}

@Composable
fun LibraryGrid(
    filteredItems: List<UserFile>,
    viewModel: LibraryViewModel,
    onDeleteModeChange: (Boolean) -> Unit,
    onCardSelected: (UserFile) -> Unit
) {
    var selectedFile by remember { mutableStateOf<UserFile?>(null) }
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()

    selectedFile?.let { file ->
        val localFile = File(context.filesDir, "user_files/${file.name}")

        if (isLoading) {
            // Show a loading indicator while checking or processing the file
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (localFile.exists()) {
            // Render the PDF from the local file
            Box(modifier = Modifier.fillMaxSize()) {
                PdfRendererViewCompose(
                    modifier = Modifier.fillMaxSize(),
                    uri = Uri.fromFile(localFile) // Use the local file's URI
                )
                FloatingActionButton(
                    onClick = { selectedFile = null },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                ) {
                    Text("Close")
                }
            }
        } else {
            // If the file is not found locally, show an error and allow re-selection
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("File not found locally. Please reselect or reupload.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        // Trigger the file save process
                        viewModel.saveAndAddFile(context, Uri.parse(file.uri))
                    }) {
                        Text("Retry")
                    }
                }
            }
        }
    } ?: BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp) // Restrict max height
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val cardSize: Dp = (screenWidth - 32.dp) / 3 // Calculate card size dynamically

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(this.maxHeight), // Apply constrained height
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredItems.size) { index ->
                val file = filteredItems[index]
                Card(
                    modifier = Modifier
                        .size(cardSize, 80.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    Log.d("LibraryGrid", "Card clicked: ${file.name}")
                                    selectedFile = file // Directly set the selected file
                                    viewModel.pickFile(context, Uri.parse(file.uri))
                                },
                                onLongPress = {
                                    Log.d("LibraryGrid", "Card long-pressed: ${file.name}")
                                    onDeleteModeChange(true)
                                    onCardSelected(file)
                                }
                            )
                        },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(file.name, maxLines = 1)
                    }
                }
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
fun AnimatedSelectedCard(
    file: UserFile,
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

        Card(
            modifier = Modifier
                .size(150.dp)
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value
                ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = file.name,
                    maxLines = 1
                )
            }
        }
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
                        Log.d("LibraryGrid", "Overlay clicked")
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
    selectedCard: UserFile?,
    viewModel: LibraryViewModel,
    onDeleteConfirm: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(2f)
    ) {
        DeleteButton(
            selectedCard = selectedCard,
            viewModel = viewModel,
            onDeleteConfirm = onDeleteConfirm,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
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
                    viewModel.deleteFile(file.id)
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
