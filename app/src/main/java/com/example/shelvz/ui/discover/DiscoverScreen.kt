package com.example.shelvz.ui.discover

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Discover") }) }
    ) { paddingValues ->
        Button(
            onClick = { navController.navigate("destination_route") },
            modifier = Modifier.padding(paddingValues)
        ) {
            Text("Navigate")
        }
    }
}