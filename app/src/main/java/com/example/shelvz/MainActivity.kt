package com.example.shelvz
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.shelvz.ui.discover.DiscoverScreen
import com.example.shelvz.ui.library.LibraryScreen
import com.example.shelvz.ui.login.LoginScreen
import com.example.shelvz.ui.user.AchievementsScreen
import com.example.shelvz.ui.user.SettingsScreen
import com.example.shelvz.ui.user.UserScreen
import com.example.shelvz.ui.user.WishlistScreen
import com.example.shelvz.util.ShelvzTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShelvzTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "library"
                ) {
                    composable("login") { LoginScreen(navController) }
                    composable("user") { UserScreen(navController) }
                    composable("discover") { DiscoverScreen(navController) }
                    composable("library") { LibraryScreen(navController) }
                    composable("wishlist") { WishlistScreen(navController) }
                    composable("achievements") { AchievementsScreen(navController) }
                    composable("settings") { SettingsScreen(navController) }
                }

            }
        }
    }
}
