package com.example.shelvz.ui.library

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.shelvz.data.model.UserFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelvz.data.model.User
import com.example.shelvz.data.repository.FileRepository
import com.example.shelvz.data.repository.UserRepository
import com.example.shelvz.util.MyResult
import com.rajat.pdfviewer.compose.PdfRendererViewCompose
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val fileRepository: FileRepository,
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // State to hold the currently logged-in user
    private val _loggedInUser = MutableStateFlow<User?>(null)
    val loggedInUser: StateFlow<User?> = _loggedInUser.asStateFlow()

    // State to hold the list of files
    private val _fileList = MutableStateFlow<List<UserFile>>(emptyList())
    val fileList: StateFlow<List<UserFile>> = _fileList.asStateFlow()

    // State to handle loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedFile = MutableStateFlow<UserFile?>(null)
    val selectedFile = _selectedFile.asStateFlow()

    // Allows filter row to sort by all uploaded filetypes
    val filters: StateFlow<List<String>> = _fileList.map { files ->
        // capitalize
        val types = files.map { it.type.replaceFirstChar { char -> char.uppercaseChar() } }.distinct()
        listOf("All") + types
    }.stateIn(viewModelScope, SharingStarted.Lazily, listOf("All"))


    // Load the logged-in user and their associated files
    init {
        loadLibrary()
    }

    // ====================
    // User Management
    // ====================
    fun loadLibrary() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Get the logged-in user
                val user = userRepository.getLoggedInUser().firstOrNull()
                _loggedInUser.value = user
                // Remove duplicate files
                _fileList.value = user?.let {
                    removeDuplicateFiles(fileRepository.getFilesByUser(it.id))
                } ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace() // Log or handle the exception as needed
                _fileList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Refresh the library
    // delay: allows time for the refreshing state to update the UI
    fun refreshLibrary() {
        viewModelScope.launch {
            _isLoading.value = true
            loadLibrary()
            _isLoading.value = false
        }
    }

    // ====================
    // File Operations
    // ====================

    // Add a new file to the library
    // Added runBlocking to prevent function from being 'suspend'
    private fun addFile(file: UserFile): MyResult<Unit> {
        return try {
            viewModelScope.launch {
                val currentFiles = fileRepository.getFilesByUser(file.userId)
                if (currentFiles.any { it.uri == file.uri }) {
                    throw Exception("File already exists in the library")
                }
                fileRepository.addFile(file)
                _fileList.value += file
            }
            MyResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("LibraryViewModel", "Error adding file: ${e.message}")
            MyResult.Error(e)
        }
    }

    private fun saveFileToLocal(context: Context, uri: Uri): File? {
        return try {
            val fileName = getFileName(uri, context.contentResolver) ?: "unknown_file"
            val inputStream = context.contentResolver.openInputStream(uri)
            val storageDir = File(context.filesDir, "user_files")
            if (!storageDir.exists()) storageDir.mkdirs()

            val destinationFile = File(storageDir, fileName)
            inputStream?.use { input ->
                destinationFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("LibraryViewModel", "File saved locally: ${destinationFile.absolutePath}")
            destinationFile
        } catch (e: Exception) {
            Log.e("LibraryViewModel", "Error saving file locally: ${e.message}")
            null
        }
    }

    fun saveAndAddFile(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true

                val localFile = saveFileToLocal(context, uri)
                if (localFile != null) {
                    val fileName = localFile.name
                    val fileSize = localFile.length()

                    val newFile = UserFile(
                        userId = loggedInUser.value?.id ?: UUID.randomUUID(),
                        uri = localFile.absolutePath, // Use local path instead of URI
                        name = fileName,
                        mime = "application/pdf",
                        type = "pdf",
                        size = fileSize
                    )

                    addFile(newFile)
                    Log.d("LibraryViewModel", "File saved and added: ${newFile.name}")
                }

            } catch (e: Exception) {
                Log.e("LibraryViewModel", "Error saving file: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markFileAsRecentlyOpened(file: UserFile) {
        viewModelScope.launch(Dispatchers.IO) {
            loggedInUser.value?.let { user ->
                val updatedRecentlyOpened = user.recentlyOpenedFiles.toMutableList()

                // Remove the file if it already exists
                updatedRecentlyOpened.removeAll { it.uri == file.uri }

                // Add the file to the top
                updatedRecentlyOpened.add(0, file)

                // Limit the list to 5 items
                if (updatedRecentlyOpened.size > 5) {
                    updatedRecentlyOpened.removeAt(updatedRecentlyOpened.lastIndex)
                }

                // Update the user in the database
                val updatedUser = user.copy(recentlyOpenedFiles = updatedRecentlyOpened)
                userRepository.updateUser(updatedUser)

                // Update the state
                _loggedInUser.value = updatedUser

                Log.d("LibraryViewModel", "Marked file as recently opened: ${file.name}")
            }
        }
    }

    fun deleteFile(fileId: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Remove file from the library
                fileRepository.deleteFile(fileId)

                // Remove file from the recentlyOpenedFiles list
                loggedInUser.value?.let { user ->
                    val updatedRecentlyOpened = user.recentlyOpenedFiles.filterNot { it.id == fileId }
                    userRepository.updateUser(user.copy(recentlyOpenedFiles = updatedRecentlyOpened))
                    Log.d("LibraryViewModel", "File removed from recently opened: $fileId")
                }

                // Reload the library
                loadLibrary()
            } catch (e: Exception) {
                Log.e("LibraryViewModel", "Error deleting file: ${e.message}", e)
            }
        }
    }

    private fun removeDuplicateFiles(files: List<UserFile>): List<UserFile> {
        return files.distinctBy { it.uri }
    }


    // ====================
    // Utility Functions
    // ====================


//    fun clearSelectedFile() {
//        _selectedFile.value = null
//    }


    private fun getFileName(uri: Uri, contentResolver: ContentResolver): String? {
        var name: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }

    private fun getFileSize(uri: Uri, contentResolver: ContentResolver): Long? {
        var size: Long? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst() && sizeIndex >= 0) {
                size = cursor.getLong(sizeIndex)
            }
        }
        return size
    }
}