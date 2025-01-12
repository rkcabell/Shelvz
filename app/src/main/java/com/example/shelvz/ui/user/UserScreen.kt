package com.example.shelvz.ui.user

import BottomBar
import com.example.shelvz.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
//import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.shelvz.data.model.User
import com.example.shelvz.ui.login.LoginViewModel
import kotlinx.coroutines.delay
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(navController: NavController,
               userViewModel: UserViewModel = hiltViewModel(),
               loginViewModel: LoginViewModel = hiltViewModel()
) {
    val user by userViewModel.loggedInUser.collectAsState()

    LaunchedEffect(user) {
        println("User in UserScreen: $user")
    }

    Scaffold(
        bottomBar = { BottomBar(navController)},
        topBar = {
            TopAppBar(
                title = { Text(text = "", color = Color.White)},
                actions = {
                    IconButton(
                        onClick = {
                            loginViewModel.logout()
                            navController.navigate("login") {
                                popUpTo("user") { inclusive = true }
                            }
                        }) {

                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = Color.White
                )
            ) }
    ) { padding ->
        // Content starts here
        Column(modifier = Modifier.padding(padding)) {
            val focusManager = LocalFocusManager.current

            // Profile Section
            Box(modifier = Modifier.fillMaxWidth()) {
                // Banner Image
                Image(
                    painter = painterResource(id = R.drawable.banner_image), //placeholder image
                    contentDescription = "Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp), // Match the Box height
                    contentScale = ContentScale.Crop
                )

                // Profile Picture
                Image(
                    painter = painterResource(id = R.drawable.profile_image), //placeholder image
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .align(Alignment.Center)
                )
            }

            // Black Section
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Profile",
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(50))
                    .align(Alignment.End)
                    .clickable(onClick = { /* Handle Click */ })
                    .padding(12.dp),
                tint = Color.White
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { focusManager.clearFocus() }
                    .padding(top = 24.dp) // Place below the profile picture
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Username and Stats
                    item {
                        if (user != null) {
                            Text(
                                text = user?.name ?: "Unknown User",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                        } else {
                            Text(text = "Loading user details...")
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "0 Books Read", color = Color.White)
                            Text(
                                text = user?.dob?.let { "Joined ${it.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))}" }
                                    ?: "Joined Unknown Date",
                                color = Color.White
                            )
                        }
                    }

                    item {
                        BioSection(
                            user = user,
                            updateBio = { newBio -> userViewModel.updateBio(newBio) }
                        )
                    }


                    item {
                        val menuItems = listOf(
                            Pair(Icons.Default.Favorite, "Wishlist"),
                            Pair(Icons.Default.Star, "My Favorites"),
                            Pair(Icons.Default.MilitaryTech, "Achievements"),
                            Pair(Icons.Default.Settings, "Settings")
                        )

                        MenuBlock(menuItems = menuItems) { selectedItem ->
                            println("Clicked on: $selectedItem") // Handle item click
                        }
                    }

                }
            }
        }
    }
}

//
//@Composable
//fun MenuItem(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
//    Column(
//        modifier = Modifier.fillMaxWidth()
//    ) {
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 8.dp)
//                .clip(RoundedCornerShape(12.dp))
//                .background(MaterialTheme.colorScheme.primary)
//                .clickable(onClick = onClick)
//                .padding(horizontal = 16.dp, vertical = 12.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Icon(
//                imageVector = icon,
//                contentDescription = label,
//                modifier = Modifier.size(24.dp),
//                tint = Color.White // Icon color
//            )
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            Text(
//                text = label,
//                style = MaterialTheme.typography.bodyLarge,
//                color = Color.White
//            )
//        }
//        // Bottom divider for each item
//        HorizontalDivider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.5f))
//    }
//}

@Composable
fun MenuBlock(
    menuItems: List<Pair<ImageVector, String>>,
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF3b3b3b)) //ARGB
    ) {
        menuItems.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item.second) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = item.first,
                    contentDescription = item.second,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.second,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            // Add a divider between items, but not after the last item
            if (index < menuItems.size - 1) {
                HorizontalDivider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
fun BioSection(
    user: User?,
    updateBio: (String) -> Unit
) {

    // State to manage the text field's value
    var bioText by remember { mutableStateOf(user?.bio ?: "") }
    val charCountMax = 200
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Update bioText whenever user?.bio changes
    LaunchedEffect(user?.bio) {
        bioText = user?.bio ?: ""
    }

    // BasicTextField with focus change handling
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(8.dp) // Add padding around the text field
    ) {
        BasicTextField(
            value = bioText,
            onValueChange = {
                if (it.length <= charCountMax) {
                    bioText = it
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused && bioText != user?.bio) {
                        // Call updateBio when the field loses focus and the bio has changed
                        updateBio(bioText)
                    }
                }
                .onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyUp && event.key == Key.Enter) {
                        // Ignore "Enter" key to avoid unintended behavior
                        true // Consume the event
                    } else {
                        false // Pass other key events through
                    }
                },
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if (bioText.isEmpty()) {
                        Text(
                            text = "Enter your bio here...",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    innerTextField()
                }
            }
        )
        // Character count below the text field
        Text(
            text = "${bioText.length}/$charCountMax",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.End) // Align the character count to the right
        )
    }
}
