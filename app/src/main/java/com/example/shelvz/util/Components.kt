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
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
    val thumbnailRes = Thumbnails.getThumbnail(subject)

    Card(
        modifier = Modifier
            .width(150.dp)
            .height(200.dp),
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
                Text(
                    text = bookTitle,
                    fontSize = 14.sp,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Clip,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailedBookCardPreview(){
    val bookTitle = "Cookbook for dummies"
    val subject = "Health"
    DetailedBookCard(bookTitle, subject)
}

@Preview(showBackground = true)
@Composable
fun BookCardPreview(){
    val bookTitle = "Cookbook for dummies"
    BookCard(bookTitle)
}

@Preview(showBackground = true)
@Composable
fun MediaSearchBarPreview() {
    var queryText by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    MediaSearchBar(
        queryText = queryText,
        onQueryChange = { queryText = it },
        isExpanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    )
}