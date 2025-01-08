package com.example.shelvz.ui.login

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shelvz.data.datastore.DataStoreManager
import com.example.shelvz.data.model.User
import com.example.shelvz.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository, private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val _loginResult = MutableStateFlow<com.example.shelvz.util.Result<User>?>(null)
    val loginResult: StateFlow<com.example.shelvz.util.Result<User>?> = _loginResult
    val isLoggedIn = dataStoreManager.isLoggedIn

    fun createAccount(user: User) {
        viewModelScope.launch {
            userRepository.insertUser(user)
        }
    }

    fun validateLogin(username: String, password: String) {
        viewModelScope.launch {
            try {
                val result = userRepository.login(username, password)
                if (result is com.example.shelvz.util.Result.Success) {
                    dataStoreManager.setUserLoginStatus(true)
                    saveCredentials(username, password)
                }
                _loginResult.value = result
            } catch (e: Exception) {
                _loginResult.value = com.example.shelvz.util.Result.Error(e)
            }
        }
    }

    private fun saveCredentials(username: String, password: String) {
        viewModelScope.launch {
            dataStoreManager.saveCredentials(username, password)
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.setUserLoginStatus(false)
            dataStoreManager.logout()
        }
    }
}