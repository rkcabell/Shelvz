import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class Screen(val route: String, val label: String) {
    object Discover : Screen("discover", "Discover")
    object Library : Screen("library", "Library")
    object User : Screen("user", "User")
}

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        Screen.Discover,
        Screen.Library,
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

