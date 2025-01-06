package com.example.shelvz.ui.library
import BottomBar
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import com.example.shelvz.R
import com.example.shelvz.util.ShelvzTheme

// Main Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(navController: NavController) {
    Scaffold(
        bottomBar = {BottomBar(navController)},
        topBar = { LibraryAppBar() }
    ) {paddingValues ->
        LibraryPage(modifier = Modifier.padding(paddingValues))
    }
}



@Composable
fun LibraryPage(modifier: Modifier = Modifier) {
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
//        Text(
//            text = "Filter",
//            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal)
//        )
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dynamically calculated card size for screen size conform
            val cardSpacing = 8.dp * 4
            val cardSize: Dp = (screenWidth - cardSpacing) / 3

            items(filteredItems.size) { index ->
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
                        interactionSource =  interactionSource,
                        indication = LocalIndication.current
                    ) {
                        Log.d("CardPress", "Card clicked at index: $index")
                    },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = filteredItems[index])
                    }
                }
            }
        }
    }
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

@Preview(showBackground = true)
@Composable
fun LibraryPagePreview() {
    LibraryPage()
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
        SearchBar(
            query = queryText,
            onQueryChange = { queryText = it },
            onSearch = {/* handle search */},
            active = isExpanded,
            onActiveChange = { isExpanded = it },
            enabled = true,
            placeholder = {
                Text(stringResource(id = R.string.search_for_media))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = stringResource(R.string.account)
                )
            },
            interactionSource = remember { MutableInteractionSource() },
            modifier = if (isExpanded) Modifier.fillMaxWidth() else Modifier
        ) {
            // Additional content for expanded SearchBar (if any)
        }
    }
}