package com.example.shelvz.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shelvz.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaSearchBar(
    queryText: String,
    onQueryChange: (String) -> Unit,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {

    SearchBar(
        inputField = {
            TextField(
                value = queryText,
                onValueChange = onQueryChange,
                placeholder = { Text(stringResource(id = R.string.search_for_media)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.search_for_media)
                    )
                },
                trailingIcon = {
                    Row {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            modifier = Modifier.clickable { onQueryChange("") }
                        )
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = stringResource(id = R.string.account)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        expanded = isExpanded,
        onExpandedChange = onExpandedChange,
        modifier = if (isExpanded) Modifier.fillMaxWidth() else Modifier,
        tonalElevation = SearchBarDefaults.TonalElevation,
        shadowElevation = SearchBarDefaults.ShadowElevation,
        colors = SearchBarDefaults.colors(),
    ) {
        // Content for expanded SearchBar
        Column {
            Text(text = "Suggested Media 1")
            Text(text = "Suggested Media 2")
        }
    }
}

@Composable
fun BookCard(bookTitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = bookTitle, fontSize = 16.sp, color = Color.White)
        }
    }
}

@Composable
fun DetailedBookCard(bookTitle: String, subject: String) {
    val thumbnailRes = when (subject) {
        "Arts" -> R.drawable.thumbnail_arts
        "Animals" -> R.drawable.thumbnail_animals
        "Fiction" -> R.drawable.thumbnail_fiction
        "Science & Mathematics" -> R.drawable.thumbnail_science
        "Business & Finance" -> R.drawable.thumbnail_business
        "Children's" -> R.drawable.thumbnail_children
        "History" -> R.drawable.thumbnail_history
        "Health & Wellness" -> R.drawable.thumbnail_health
        "Biography" -> R.drawable.thumbnail_biography
        "Social Sciences" -> R.drawable.thumbnail_social_sciences
        "Places" -> R.drawable.thumbnail_places
        "Textbooks" -> R.drawable.thumbnail_textbooks
        else -> R.drawable.thumbnail_default
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = thumbnailRes),
                contentDescription = "$subject thumbnail",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = bookTitle, fontSize = 16.sp, color = Color.White)
            }
        }
    }
}