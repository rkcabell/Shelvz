package com.example.shelvz.ui


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shelvz.util.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(navController: NavController) {
    Scaffold(
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