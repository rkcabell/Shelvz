package com.example.shelvz.ui.login

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.shelvz.data.model.User
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDate
import java.util.UUID

@Composable
fun CreateAccountScreen(navController: NavHostController, loginViewModel: LoginViewModel = hiltViewModel()) {

    var username by remember { mutableStateOf("") }
    var verifyPassword by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val passwordsMatch = (password == verifyPassword && password.isNotBlank())
    val createButtonEnabled = username.isNotBlank() && passwordsMatch
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current


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
                text = "Create Account",
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

            Spacer(modifier = Modifier.height(32.dp))

            // Password Field
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Verify Password Field
            TextField(
                value = verifyPassword,
                onValueChange = { verifyPassword = it },
                label = { Text("Verify Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                isError = !passwordsMatch && password.isNotBlank(),
                trailingIcon = {
                    if (!passwordsMatch && verifyPassword.isNotBlank()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Passwords do not match",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            if (!passwordsMatch && verifyPassword.isNotBlank()) {
                Text(
                    text = "Passwords do not match",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Create Account Button
            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Username or password cannot be empty.")
                        }
                    } else {
                        loginViewModel.createAccount(username, password)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Account created successfully!")
                        }
                        navController.navigate("login") {
                            popUpTo("createAccount") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = createButtonEnabled
            ) {
                Text(text = "Create Account")
            }

            //Route back to login
            TextButton(
                onClick = {navController.popBackStack()},
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Back to Login")
            }
        }
    }
}