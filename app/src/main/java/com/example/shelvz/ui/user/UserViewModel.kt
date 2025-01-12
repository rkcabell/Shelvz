package com.example.shelvz.ui.user

import com.example.shelvz.util.MyResult
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shelvz.data.model.User
import com.example.shelvz.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

/*
Stores and manages the ModelUiState.
Fetches or updates data via the ModelRepository.
Processes UI events and updates the state reactively.
StateFlow over LiveData
 */

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    // Private mutable StateFlow to hold the current logged-in user
    // Allows local modifications (e.g., updating the bio) without re-fetching the user from the database.
    private val _userState = MutableStateFlow<User?>(null)

    // Public immutable state to expose user data
    val userState: StateFlow<User?> = _userState

    val loggedInUser: StateFlow<User?> = userRepository.getLoggedInUser().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    init {
        // Initialize _userState with the logged-in user's data
        viewModelScope.launch {
            userRepository.getLoggedInUser().collect { user ->
                println("Logged-in user in UserViewModel: $user")
                _userState.value = user
            }
        }
    }

    // optional
//    fun getLoggedInUser(): StateFlow<User?> {
//        return userRepository.getLoggedInUser().stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = null
//        )
//    }

    fun refreshUserData() {
        viewModelScope.launch {
            try {
                val user = userRepository.getLoggedInUser().firstOrNull()
                _userState.value = user // Update local state
            } catch (e: Exception) {
                println("Error refreshing user data: ${e.message}")
            }
        }
    }

    fun getUserData(userId: UUID) {
        viewModelScope.launch {
            when (val result = userRepository.getUserById(userId)) {
                is MyResult.Success -> {
                    _userState.value = result.data
                }
                is MyResult.Error -> {
                    println("Error fetching user data: ${result.exception.message}")
                }
                is MyResult.Loading -> {
                    // Optionally handle loading state
                }
            }
        }
    }


    fun deleteUser(user: User) {
        viewModelScope.launch {
            userRepository.deleteUser(user)
        }
    }


    // Update the bio of the user
    fun updateBio(newBio: String) {
        viewModelScope.launch {
            val user = _userState.value
            if (user != null) {
                try {
                    val updatedUser = user.copy(bio = newBio)
                    val result = userRepository.updateBio(updatedUser.id, newBio)
                    if (result is MyResult.Success) {
                        _userState.value = updatedUser // Update local state with the new bio
                    } else if (result is MyResult.Error) {
                        println("Failed to update bio: ${result.exception.message}")
                    }
                } catch (e: Exception) {
                    println("Error updating bio: ${e.message}")
                }
            } else {
                println("No user is currently logged in.")
            }
        }
    }
}
