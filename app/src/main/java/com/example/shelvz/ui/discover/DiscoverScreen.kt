package com.example.shelvz.ui.discover
import BottomBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomBar(navController)},
        topBar = { TopAppBar(title = { Text("Discover", fontWeight = FontWeight.Bold, fontSize = 40.sp) }) }
    ) { paddingValues ->
        Button(
            onClick = { navController.navigate("details") },
            modifier = Modifier.padding(paddingValues)
        ) {
            Text("Click to Crash App")
        }
    }
}