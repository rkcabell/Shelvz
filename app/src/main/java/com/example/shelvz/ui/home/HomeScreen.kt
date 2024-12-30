package com.example.shelvz.ui.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(
            title = { Text("Home", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
        )}
    ) { paddingValues ->
        Button(
            onClick = { },
            modifier = Modifier.padding(paddingValues)
        ) {

        }
    }
}
