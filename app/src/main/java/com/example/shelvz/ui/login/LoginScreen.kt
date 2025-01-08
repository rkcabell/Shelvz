package com.example.shelvz.ui.login

import BottomBar
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shelvz.data.model.User
import com.example.shelvz.util.Result
import org.mindrot.jbcrypt.BCrypt

@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel = hiltViewModel()
){
    val loginResult by loginViewModel.loginResult.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(loginResult) {
        when (val result = loginResult) {
            is com.example.shelvz.util.Result.Success -> {
                isLoading = false
                navController.navigate("library") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is com.example.shelvz.util.Result.Error -> {
                isLoading = false
                // Show Toast for login failure
                Toast.makeText(context, "Login failed: ${result.exception.message}", Toast.LENGTH_SHORT).show()
            }
            else -> { /* Do nothing */ }
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Username Field
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = {

                    when {
                        username.isBlank() -> {
                            Toast.makeText(context, "Username cannot be empty.", Toast.LENGTH_SHORT)
                                .show()
                        }

                        password.isBlank() -> {
                            Toast.makeText(context, "Password cannot be empty.", Toast.LENGTH_SHORT)
                                .show()
                        }
                        else -> {
                            isLoading = true
                            loginViewModel.validateLogin(username, password)
                        }
                    }

                    if (username.isNotBlank() && password.isNotBlank()) {
                        isLoading = true
                        loginViewModel.validateLogin(username, password)
                    } else {
                        errorMessage = "Username and password cannot be empty."
                    }
                          },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                Text(text = "Login")
            }
            }

            TextButton(
                onClick = {navController.navigate("createAccount")},
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Create Account")
            }

        }
    }
}