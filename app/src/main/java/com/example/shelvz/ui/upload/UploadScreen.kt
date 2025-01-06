package com.example.shelvz.ui.upload


import BottomBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomBar(navController)},
        topBar = { TopAppBar(title = { Text("Upload") }) }
    ) { paddingValues ->
        Button(
            onClick = { navController.navigate("destination_route") },
            modifier = Modifier.padding(paddingValues)
        ) {
            Text("Navigate")
        }
    }
}