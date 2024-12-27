package com.example.shelvz.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.shelvz.util.Screen

@Composable
fun LibraryPage() {
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
        // Search Bar
        BasicTextField(
            value = searchText,
            onValueChange = { searchText = it },
            textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(50.dp)
                .background(Color.LightGray, shape = MaterialTheme.shapes.medium)
                .padding(horizontal = 16.dp),
            decorationBox = { innerTextField ->
                if (searchText.isEmpty()) {
                    Text("Search...", color = Color.Gray)
                }
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

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
            items(recentlyOpened.size) { index ->
                Card(
                    modifier = Modifier.size(120.dp, 60.dp),
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
            modifier = Modifier.fillMaxWidth()
        ) {
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredItems.size) { index ->
                Card(
                    modifier = Modifier.size(100.dp),
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

@Preview(showBackground = true)
@Composable
fun LibraryPagePreview() {
    LibraryPage()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Library") }) }
    ) { paddingValues ->
        Button(
            { navController.navigate(Screen.Discover.route) },
            modifier = Modifier.padding(paddingValues).padding(16.dp)
        ) {
            Text("Library Action Placeholder")
        }
    }
}