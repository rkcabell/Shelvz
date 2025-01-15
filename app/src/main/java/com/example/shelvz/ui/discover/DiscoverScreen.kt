package com.example.shelvz.ui.discover
import BottomBar
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.shelvz.data.model.Book
import com.example.shelvz.util.BookCard
import com.example.shelvz.util.DetailedBookCard
import com.example.shelvz.util.MediaSearchBar
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DiscoverScreen(navController: NavController) {
    val viewModel: DiscoverViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        bottomBar = {BottomBar(navController)},
        topBar = { DiscoverAppBar(viewModel) },
        content = { paddingValues ->
            DiscoverPage(uiState, Modifier.padding(paddingValues))
        }
    )
}

@Composable
fun DiscoverAppBar(viewModel: DiscoverViewModel) {
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
            onQueryChange = {
                queryText = it
                viewModel.searchBooks(queryText)},
            isExpanded = isExpanded,
            onExpandedChange = { isExpanded = it }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscoverPage(uiState: DiscoverUiState, modifier: Modifier = Modifier) {



    when (uiState) {
        is DiscoverUiState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }
        is DiscoverUiState.Success -> {
            val booksBySubject = uiState.books.groupBy { it.subject }
            BookGrid(booksBySubject = booksBySubject, modifier = modifier)
        }
        is DiscoverUiState.Error -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.fillMaxSize()
            ) {
                Text(text = "Error: ${uiState.message}", color = MaterialTheme.colorScheme.error)
            }
        }
    }

}

@Composable
fun BookGrid(booksBySubject: Map<String, List<Book>>, modifier: Modifier = Modifier) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        booksBySubject.forEach { (subject, books) ->
            item {
                Column {
                    Text(
                        text = subject,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(books.take(5)) { book ->
                            DetailedBookCard(bookTitle = book.title, book.subject)
                        }
                    }
                }
            }
        }
    }
}

//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun BookGrid(books: List<Book>, modifier: Modifier = Modifier) {
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        contentPadding = PaddingValues(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp),
//        horizontalArrangement = Arrangement.spacedBy(16.dp),
//        modifier = modifier.fillMaxSize()
//    ) {
//        items(books) { book ->
//            DetailedBookCard(bookTitle = book.title, book.subject)
//        }
//    }
//}