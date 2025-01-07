package com.example.shelvz
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.shelvz.ui.login.CreateAccountScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking



@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val Context.dataStore by preferencesDataStore(name = "user_preferences")
    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShelvzTheme {
                val navController = rememberNavController()
                val isLoggedIn = checkUserLoginStatus()
                val startDestination = if (isLoggedIn) "library" else "login"
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable("login") { LoginScreen(navController, ) }
                    composable("createAccount") { CreateAccountScreen(navController) }
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
    private fun checkUserLoginStatus(): Boolean {
        return runBlocking {
            val preferences = dataStore.data.first()
            preferences[IS_LOGGED_IN] ?: false
        }
    }

    private suspend fun setUserLoginStatus(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
        }
    }
}

