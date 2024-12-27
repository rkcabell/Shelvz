package com.example.shelvz.util
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.shelvz.ui.*
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


sealed class Screen(val route: String, val label: String) {
    object Home : Screen("home", "Home")
    object Discover : Screen("discover", "Discover")
    object Library : Screen("library", "Library")
    object Upload : Screen("upload", "Upload")
    object User : Screen("user", "User")
}

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController = navController) }
        composable(Screen.Discover.route) { DiscoverScreen(navController = navController) }
        composable(Screen.Library.route) { LibraryScreen(navController = navController) }
        composable(Screen.Upload.route) { UploadScreen(navController = navController) }
        composable(Screen.User.route) { UserScreen(navController = navController) }
    }
}


@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Discover,
        Screen.Library,
        Screen.Upload,
        Screen.User
    )
    val currentBackStackEntry = navController.currentBackStackEntryAsState()

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                label = { Text(screen.label) },
                selected = currentBackStackEntry.value?.destination?.route == screen.route,
                onClick = { navController.navigate(screen.route) },
                icon = {
                    // Add icons here if you have them
                    // Icon(painter = painterResource(id = R.drawable.icon), contentDescription = screen.label)
                    Text(screen.label.take(1)) // Placeholder
                }
            )
        }
    }
}

@Composable
fun MainScaffold() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
        MainNavHost(navController = navController        )
        }
    }
}