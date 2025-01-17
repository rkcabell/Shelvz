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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.shelvz.R
import com.example.shelvz.data.model.File
import com.example.shelvz.util.MediaSearchBar
import com.example.shelvz.util.ShelvzTheme

// Main Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(navController: NavController, viewModel: LibraryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val user by viewModel.loggedInUser.collectAsState()
    val fileList by viewModel.fileList.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//
//    // Display loading spinner if necessary
//    if (isLoading) {
//        CircularProgressIndicator()
//    } else {
//        Column {
//            Text(text = "Welcome, ${user?.name ?: "Guest"}")
//            LazyColumn {
//                items(fileList) { file ->
//                    Text(text = file.name)
//                }
//            }
//        }
//    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                Log.d("LibraryPage", "Selected file URI: $uri")
                handleSelectedFile(uri, viewModel, contentResolver)
            } else {
                Log.e("LibraryPage", "File selection cancelled or failed.")
            }
        }
    )

    Scaffold(
        bottomBar = {BottomBar(navController)},
        topBar = { LibraryAppBar() }
    ) {paddingValues ->
        LibraryPage(modifier = Modifier.padding(paddingValues), viewModel, launcher)
    }
}


@Composable
fun LibraryPage(modifier: Modifier = Modifier, viewModel: LibraryViewModel, launcher: ActivityResultLauncher<Array<String>>) {
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val recentlyOpened = listOf("Book 1", "Movie 1", "Book 2")
    val libraryItems = listOf(
        "Book A", "Book B", "Book C",
        "Movie X", "Movie Y", "Movie Z"
    )
    val filters = listOf("All", "Books", "Movies")

    val filteredItems = when (selectedFilter) {
        "Books" -> libraryItems.filter { it.startsWith("Book") }
        "Movies" -> libraryItems.filter { it.startsWith("Movie") }
        else -> libraryItems
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            //Shared for dynamic card size, current = Column
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp

            // Space under search bar (TopAppBar)
            Spacer(modifier = Modifier.height(64.dp))

            // Recently Opened LazyRow
            Text(
                text = "Recently Opened",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Dynamically calculated card size for screen size conform
                val cardSpacing = 8.dp * 4
                val cardSize: Dp = (screenWidth - cardSpacing) / 3
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


            Spacer(modifier = Modifier.height(16.dp))

            // Filters Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Text(
                        text = "Filter ",
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal)
                    )
                    Icon(
                        imageVector = Icons.Outlined.FilterList,
                        contentDescription = "Filter Icon",
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp)
                    )
                }
                items(filters.size) { index ->
                    Button(
                        onClick = { selectedFilter = filters[index] },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedFilter == filters[index]) Color.Blue else Color.Gray
                        )
                    ) {
                        Text(
                            text = filters[index],
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            // Scrollable Library Grid
            Text(
                text = "My Collection",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            val fileList by viewModel.fileList.collectAsState()

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Dynamically calculated card size for screen size conform
                val cardSpacing = 8.dp * 4
                val cardSize: Dp = (screenWidth - cardSpacing) / 3

                items(filteredItems.size) { index ->
                    val file = fileList[index]
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed = interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed.value) 0.95f else 1f,
                        label = "Card Scale Animation"
                    )
                    // Pressed Log
                    //                LaunchedEffect(isPressed.value) {
                    //                    Log.d("CardPress", "Card is pressed: ${isPressed.value}")
                    //                }
                    Card(
                        modifier = Modifier
                            .size(cardSize, 80.dp)
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = LocalIndication.current
                            ) {
                                Log.d("LibraryScreen", "File clicked: ${file.name}")
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = filteredItems[index])
                        }
                    }
                }
            }
            Box(
                modifier = Modifier.fillMaxSize()
                    .padding(bottom = 72.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = "Upload",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Blue)
                        .clickable(onClick = {
                            launcher.launch(arrayOf("*/*"))
                        })
                        .padding(12.dp),
                    tint = Color.White
                )
            }
        }
    }
}

fun handleSelectedFile(uri: Uri, viewModel: LibraryViewModel, contentResolver: ContentResolver) {
    try {
        val userId = viewModel.loggedInUser.value?.id
        if (userId != null) {
            val fileName = getFileName(uri, contentResolver)
            val mimeType = contentResolver.getType(uri)
            val fileSize = getFileSize(uri, contentResolver)

            Log.d("LibraryPage", "File metadata: Name=$fileName, Type=$mimeType, Size=$fileSize")

            if (fileName != null && mimeType != null && fileSize != null) {
                val newFile = File(
                    userId = userId,
                    uri = uri.toString(),
                    name = fileName,
                    type = mimeType,
                    size = fileSize
                )
                viewModel.addFile(newFile)
                Log.d("LibraryPage", "File added to the library: ${newFile.name}")
            } else {
                Log.e("LibraryPage", "Failed to retrieve file metadata.")
            }
        } else {
            Log.e("LibraryPage", "User not logged in.")
        }
    } catch (e: Exception) {
        Log.e("LibraryPage", "Error processing selected file: ${e.message}", e)
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



@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun LibraryAppBarPreview() {
    ShelvzTheme() {
        LibraryAppBar(
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun LibraryPagePreview() {
//    LibraryPage()
//}



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


