package com.example.shelvz.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelvz.data.model.User
import com.example.shelvz.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _loginResult = MutableStateFlow<com.example.shelvz.util.Result<User>?>(null)
    val loginResult: StateFlow<com.example.shelvz.util.Result<User>?> = _loginResult

    fun createAccount(user: User) {
        viewModelScope.launch {
            userRepository.insertUser(user)
        }
    }

    fun validateLogin(username: String, password: String) {
        viewModelScope.launch {
            try {
                val result = userRepository.login(username, password)
                _loginResult.value = result
            } catch (e: Exception) {
                _loginResult.value = com.example.shelvz.util.Result.Error(e)
            }
        }
    }
}