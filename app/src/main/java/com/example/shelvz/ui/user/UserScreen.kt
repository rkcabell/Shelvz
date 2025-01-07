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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(navController: NavController) {
//    val userViewModel: UserViewModel = viewModel() // Using default ViewModelProvider
//    val userData by userViewModel.userData.collectAsState()

    Scaffold(
        bottomBar = { BottomBar(navController)},
        topBar = {
            TopAppBar(
                title = { Text(text = "")},
                actions = {
                    IconButton(onClick = {/* Logout action */}) {}
                    Icon(Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.Black)
                },
                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.Blue,
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
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.BottomEnd)
                        .clickable(onClick = { /* Handle Click */ })
                )
            }

            // Black Section
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { focusManager.clearFocus() }
                    .padding(top = 50.dp) // Place below the profile picture
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Username and Stats
                    Text(
                        text = "Username",
//                        text = userViewModel.getUsername(userData.name),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "39 Books Read", color = Color.White)
                        Text(text = "Joined December 28, 2024", color = Color.White)
                    }

                    // Bio Section
                    BioSection()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Menu Options
                    MenuItem(icon = Icons.Default.Favorite, label = "Wishlist")
                    MenuItem(icon = Icons.Default.Star, label = "My Favorites")
                    MenuItem(icon = Icons.Default.MilitaryTech, label = "Achievements")
                    MenuItem(icon = Icons.Default.Settings, label = "Settings")
                }
            }
        }
    }
}

@Composable
fun MenuItem(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    val isClicked  = remember { mutableStateOf(false) }

    if (isClicked.value) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(50)
            isClicked.value = false // Reset state
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(if (isClicked.value) Color(0xFFFFD700) else Color.Transparent)
            .clickable {
                isClicked.value = true
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isClicked.value) Color.Black else MaterialTheme.colorScheme.surface
        )
    }
}


@Composable
fun BioSection(
) {
    // State to manage the text field's value
    var bioText by remember { mutableStateOf("Bio") }
    val charCountMax = 200
    val focusRequester = remember { FocusRequester() }

    // Has to be BasicTextField not TextField for decoration box (character count)
    BasicTextField(
        value = bioText,
        onValueChange = {
            if (it.length <= charCountMax) {
                bioText = it
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .focusRequester(focusRequester),
        textStyle = TextStyle(
            color = Color.Black
        ),

        // Character Count Tracker
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                if (bioText.isEmpty()) {
                    Text(
                        text = "Enter your bio here..."
                    )
                }
                innerTextField()
                Text(
                    text = "${bioText.length}/${charCountMax}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 4.dp)
                )
            }
        }
    )
}

    // Save the bio when focus is lost
    //TODO( Work on database, put this inside BioSection outside BasicTextField )
//    LaunchedEffect(Unit) {
//        snapshotFlow { bioText }
//            .debounce(500) // Optional debounce to prevent excessive updates
//            .collect { newBio ->
//                userViewModel.updateUserBio(userId, newBio)
//            }
//    }
//    )
