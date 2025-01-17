package com.example.shelvz.ui.library

import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.example.shelvz.data.model.File
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelvz.data.model.User
import com.example.shelvz.data.repository.FileRepository
import com.example.shelvz.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val fileRepository: FileRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // State to hold the currently logged-in user
    private val _loggedInUser = MutableStateFlow<User?>(null)
    val loggedInUser: StateFlow<User?> = _loggedInUser.asStateFlow()

    // State to hold the list of files
    private val _fileList = MutableStateFlow<List<File>>(emptyList())
    val fileList: StateFlow<List<File>> = _fileList.asStateFlow()

    // State to handle loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Load the logged-in user and their associated files
    init {
        loadLibrary()
    }

    private fun loadLibrary() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Get the logged-in user
                val user = userRepository.getLoggedInUser().firstOrNull()
                _loggedInUser.value = user

                // Load files associated with the user
                if (user != null) {
                    _fileList.value = fileRepository.getFilesByUser(user.id)
                } else {
                    _fileList.value = emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log or handle the exception as needed
                _fileList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Add a new file to the library
    fun addFile(file: File) {
        viewModelScope.launch {
            try {
                fileRepository.addFile(file)
                _fileList.value = fileRepository.getFilesByUser(file.userId)
            } catch (e: Exception) {
                e.printStackTrace() // Handle errors if needed
            }
        }
    }

    // Delete a file from the library
    fun deleteFile(fileId: UUID) {
        viewModelScope.launch {
            try {
                fileRepository.deleteFile(fileId)
                val userId = _loggedInUser.value?.id
                if (userId != null) {
                    _fileList.value = fileRepository.getFilesByUser(userId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Refresh the library
    fun refreshLibrary() {
        loadLibrary()
    }
}
