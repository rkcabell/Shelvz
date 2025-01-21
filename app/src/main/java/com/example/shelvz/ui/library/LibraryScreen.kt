package com.example.shelvz.ui.library
import BottomBar
import android.content.ContentResolver
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

import com.example.shelvz.data.model.UserFile
import com.example.shelvz.util.MediaSearchBar
import com.example.shelvz.util.MyResult
import com.example.shelvz.util.ShelvzTheme
import kotlinx.coroutines.launch

// Main Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val contentResolver = LocalContext.current.contentResolver
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val refreshing = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            try {
                if (uri != null) {
                    Log.d("FilePicker", "Selected file URI: $uri")
                    val error = handleSelectedFile(uri, viewModel, contentResolver)
                    errorMessage = error
                } else {
                    Log.e("FilePicker", "File selection cancelled or failed.")
                }
            } catch (e: Exception) {
                Log.e("FilePicker", "Error handling selected file: ${e.message}", e)
            }
        }
    )


    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short)
            errorMessage = null
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = refreshing.value,
            onRefresh = {
                Log.d("RefreshIndicator", "Refreshing state: ${refreshing.value}")
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
            LibraryPage(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
                launcher = launcher
            )
        }
    }
}

// manages the UI logic for displaying and interacting with the library's file list
@Composable
fun LibraryPage(
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel,
    launcher: ActivityResultLauncher<Array<String>>
) {
    // Fetch the full list of files from the ViewModel
    val libraryItems by viewModel.fileList.collectAsState()
    val filters by viewModel.filters.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }

    // Delete mode
    var isDeleteMode by remember { mutableStateOf(false) }
    var selectedCard by remember { mutableStateOf<UserFile?>(null) }

    // Dynamically filter the files based on the selected filter
    val filteredItems = when (selectedFilter) {
        "All" -> libraryItems
        else -> libraryItems.filter { it.type.contains(selectedFilter, ignoreCase = true) }
    }

    Box(
        modifier = modifier
        .fillMaxSize()
    ) {
        if (isDeleteMode) {
            DimmerOverlay(
                onDismiss = {
                    Log.d("LibraryGrid", "Dimmer overlay dismissed")
                    isDeleteMode = false
                    selectedCard = null
            })
        }
        if (filteredItems.isEmpty()) {
            OnEmptyLibrary(launcher)
        }
        else {
            LazyColumn(
                modifier = modifier.fillMaxSize().zIndex(if (isDeleteMode) -1f else 0f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { LibraryAppBar() }
                item { RecentlyOpenedSection() }
                item { FilterRow(filters = filters, selectedFilter = selectedFilter) { selectedFilter = it } }
                item { LibraryGrid(filteredItems,
                    viewModel,
                    isDeleteMode = isDeleteMode,
                    onDeleteModeChange = { isDeleteMode = it },
                    onCardSelected = { selectedCard = it }) }

            }
            if (!isDeleteMode) {
                    UploadButton(launcher)
            }
        }
        if (isDeleteMode) {
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
fun RecentlyOpenedSection() {
    //TODO After opening a file is supported
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
        // forEach approach
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
        // Indexing approach
//            items(filters.size) { index ->
//                val filter = filters[index]
//                Button(
//                    onClick = { onFilterSelected(filter) },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = if (selectedFilter == filter) Color.Blue else Color.Gray
//                    )
//                ) {
//                    Text(text = filter, color = Color.White)
//                }
//            }
    }
}

@Composable
fun LibraryGrid(
    filteredItems: List<UserFile>,
    viewModel: LibraryViewModel,
    isDeleteMode: Boolean,
    onDeleteModeChange: (Boolean) -> Unit,
    onCardSelected: (UserFile) -> Unit )
{
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val cardSize: Dp = (screenWidth - 32.dp) / 3

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(this.maxHeight),
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
                                    // Trigger file opening
                                    // viewModel.openFile(file)
                                },
                                onLongPress = {
                                    Log.d("LibraryGrid", "Card long-pressed: ${file.name}")
                                    onDeleteModeChange(true)
                                    onCardSelected(file)
                            })
                        },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = file.name, maxLines = 1)
                    }
                }
            }
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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadButton(launcher: ActivityResultLauncher<Array<String>>) {

    //two options for upload menu
    var showDialog by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 16.dp, bottom = 16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Icon(
            imageVector = Icons.Default.Upload,
            contentDescription = "Upload",
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Blue)
                .clickable {showDialog  = true}
//                .clickable {showSheet  = true}
                .padding(12.dp),
            tint = Color.White
        )
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Upload File") },
            text = { Text("Select a file to upload.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        launcher.launch(arrayOf("*/*"))
                        showDialog = false
                    }
                ) {
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

//    if (showSheet) {
//        ModalBottomSheet(
//            onDismissRequest = { showSheet = false }
//        ) {
//            Column(modifier = Modifier.padding(16.dp)) {
//                Text("Upload Options", style = MaterialTheme.typography.titleMedium)
//                Spacer(modifier = Modifier.height(8.dp))
//                Button(
//                    onClick = {
//                        launcher.launch(arrayOf("*/*"))
//                        showSheet = false
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text("Select File")
//                }
//                Spacer(modifier = Modifier.height(8.dp))
//                Button(
//                    onClick = { showSheet = false },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text("Cancel")
//                }
//            }
//        }
//    }

}


fun handleSelectedFile(
    uri: Uri,
    viewModel: LibraryViewModel,
    contentResolver: ContentResolver
): String? {

        val userId = viewModel.loggedInUser.value?.id

        if (userId == null) {
            Log.e("LibraryPage", "User not logged in.")
            return "User not logged in."
        }

        return try {
            val fileName = getFileName(uri, contentResolver)
            val fullMimeType = contentResolver.getType(uri) ?: "unknown/unknown"
            val (mime, type) = extractMimeAndType(fullMimeType)
            val fileSize = getFileSize(uri, contentResolver)


            if (fileName.isNullOrEmpty() || type.isEmpty() || fileSize == null) {
                Log.e("LibraryPage", "Invalid file metadata: Name=$fileName, Type=$type, Size=$fileSize")
                return "Invalid file metadata"
            }

            val newFile = UserFile(
                userId = userId,
                uri = uri.toString(),
                name = fileName,
                mime = mime,
                type = type,
                size = fileSize
            )

//            viewModel.addFile(newFile)
            val result = viewModel.addFile(newFile)
            if (result is MyResult.Error) {
                result.exception.message ?: "Failed to add file"

            } else {
                Log.d("LibraryPage", "File added to the library: ${newFile.name}")
                null // Return null on success
            }

        } catch (e: Exception) {
            Log.e("LibraryPage", "Error processing selected file: ${e.message}", e)
            "Error processing file: ${e.message}"
        }
}

fun extractMimeAndType(fullMimeType: String): Pair<String, String> {
    val parts = fullMimeType.split("/")
    val mime = parts.getOrNull(0) ?: "unknown"
    val type = parts.getOrNull(1) ?: "unknown"
    return mime to type
}

@Composable
fun HandleAddFileResult(result: MyResult<Unit>, snackbarHostState: SnackbarHostState) {
    if (result is MyResult.Error) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(
                message = result.exception.message ?: "Failed to add file",
                duration = SnackbarDuration.Short
            )
        }
    }
}


private fun getFileName(uri: Uri, contentResolver: ContentResolver): String? {
    var name: String? = null
    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && nameIndex >= 0) {
            name = cursor.getString(nameIndex)
        }
    }
    return name
}

private fun getFileSize(uri: Uri, contentResolver: ContentResolver): Long? {
    var size: Long? = null
    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        if (cursor.moveToFirst() && sizeIndex >= 0) {
            size = cursor.getLong(sizeIndex)
        }
    }
    return size
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
//            .clickable(
//                indication = null, // Disable ripple effect
//                interactionSource = remember { MutableInteractionSource() }
//            ) {
//                onDismiss() // Handle clicks outside the active elements
//            }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun LibraryAppBarPreview() {
    ShelvzTheme() {
        LibraryAppBar(
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





/*
query: Tracks the search text entered by the user.
onQueryChange: Updates the search text when the user types.
onSearch: Triggered when the user performs a search.
active: Tracks whether the SearchBar is active or inactive.
onActiveChange: Updates the active state when the user expands or collapses the SearchBar.
placeholder: Shows a hint when the search field is empty.
leadingIcon: Adds an icon at the start of the SearchBar.
trailingIcon: Adds an icon at the end of the SearchBar.
interactionSource: Tracks user interactions with the SearchBar.
modifier: Adds layout customization.
 */
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


