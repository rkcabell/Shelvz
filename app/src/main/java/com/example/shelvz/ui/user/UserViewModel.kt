package com.example.shelvz.ui.user
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelvz.data.repository.UserRepository
import kotlinx.coroutines.launch

/*
Stores and manages the ModelUiState.
Fetches or updates data via the ModelRepository.
Processes UI events and updates the state reactively.
 */

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun updateUserBio(userId: Int, newBio: String) {
        viewModelScope.launch {
            userRepository.updateBio(userId, newBio)
        }
    }
}