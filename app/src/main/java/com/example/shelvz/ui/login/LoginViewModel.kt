package com.example.shelvz.ui.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.shelvz.data.datastore.DataStoreManager
import com.example.shelvz.data.model.User
import com.example.shelvz.data.repository.UserRepository
import com.example.shelvz.util.MyResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<MyResult<Boolean>>(MyResult.Loading)
    val loginState: StateFlow<MyResult<Boolean>> = _loginState

    private var hasAttemptedLogin = false

    val isLoggedIn: StateFlow<Boolean> = userRepository.isLoggedIn.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // The flow is active and emitting values only when there are active collectors (subscribers) in the UI
    // Delay of 5 seconds prevents accidental re-subscriptions
    val loggedInUserId: StateFlow<UUID?> = userRepository.loggedInUserId.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    init {
        viewModelScope.launch {
            userRepository.getLoggedInUser().collect { user ->
                val isLoggedIn = user?.isLoggedIn ?: false
                _loginState.value = MyResult.Success(isLoggedIn)
            }
        }
    }

    fun createAccount(user: User) {
        viewModelScope.launch {
            userRepository.saveUser(user)
        }
    }

    fun createAccount(username: String, password: String) {
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank()) {
                println("Username or password cannot be empty.")
                return@launch
            }

            val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
            val user = User(
                id = UUID.randomUUID(),
                name = username,
                email = "user@example.com", // Replace with actual email if available
                password = hashedPassword,
                profilePic = null,
                dob = LocalDate.now(), // Replace with real value if available
                bio = "New user bio",
                library = emptyList(),
                favorites = emptyList(),
                favoriteGenres = emptyList(),
                isLoggedIn = true
            )

            val result = userRepository.saveUser(user)
            if (result is MyResult.Success) {
                println("Account created successfully! $user")
            } else if (result is MyResult.Error) {
                println("Failed to create account: ${result.exception.message}")
            }
        }
    }

    fun hasLoginAttempted(): Boolean = hasAttemptedLogin

    fun logout() {
        viewModelScope.launch {
            try {
                userRepository.setUserLoginStatus(false)
                userRepository.logout() // Reset the logged-in state in DataStore
                _loginState.value = MyResult.Success(false)
            } catch (e: Exception) {
                _loginState.value = MyResult.Error(e)
            }
        }
    }

    private fun saveCredentials(user: User) {
        viewModelScope.launch {
            try {
                userRepository.saveUser(user.copy(isLoggedIn = true))
            } catch (e: Exception) {
                println("Failed to save user credentials: ${e.message}")
            }
        }
    }

    fun validateLogin(username: String, password: String) {
        hasAttemptedLogin = true
        viewModelScope.launch {
            _loginState.value = MyResult.Loading
            try {
                val result = userRepository.login(username, password)
                if (result is MyResult.Success && result.data) {
                    val user = userRepository.getUserByName(username) // Fetch the user data
                    if (user != null) {
                        saveCredentials(user.copy(isLoggedIn = true)) // Save the user's credentials
                    }
                }
                _loginState.value = result
            } catch (e: Exception) {
                _loginState.value = MyResult.Error(e)
            }
        }
    }

    fun clearLoginResult() {
        _loginState.value = MyResult.Loading
    }


}

