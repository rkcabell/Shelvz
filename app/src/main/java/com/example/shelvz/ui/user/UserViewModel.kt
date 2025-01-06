package com.example.shelvz.ui.user

//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shelvz.data.model.User
import com.example.shelvz.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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


    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> get() = _userData

    fun getUsername(userId: UUID) {
        viewModelScope.launch {
            userRepository.getUsername(userId)
        }
    }

    fun insertUser(user: User) {
        viewModelScope.launch {
            userRepository.insertUser(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            userRepository.deleteUser(user)
        }
    }

    fun updateBio(userId: UUID, newBio: String) {
        viewModelScope.launch {
            userRepository.updateBio(userId, newBio)
        }
    }

}
