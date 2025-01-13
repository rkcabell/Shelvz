package com.example.shelvz.ui.discover
import BottomBar
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shelvz.util.BookCard
import com.example.shelvz.util.MediaSearchBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DiscoverScreen(navController: NavController) {
    Scaffold(
        bottomBar = {BottomBar(navController)},
        topBar = { DiscoverAppBar() },
        content = { paddingValues ->
            DiscoverPage(Modifier.padding(paddingValues))
        }
    )
}

@Composable
fun DiscoverAppBar() {
    var queryText by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        MediaSearchBar(
            queryText = queryText,
            onQueryChange = { queryText = it },
            isExpanded = isExpanded,
            onExpandedChange = { isExpanded = it }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscoverPage(modifier: Modifier = Modifier) {
    val placeholderBooks = List(20) { "Book $it" }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(placeholderBooks) { book ->
            BookCard(bookTitle = book)
        }
    }
}

