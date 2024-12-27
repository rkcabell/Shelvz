package com.example.shelvz.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("User") }) }
    ) { paddingValues ->
        Button(
            onClick = { navController.navigate("destination_route") },
            modifier = Modifier.padding(paddingValues)
        ) {
            Text("Navigate")
        }
    }
}