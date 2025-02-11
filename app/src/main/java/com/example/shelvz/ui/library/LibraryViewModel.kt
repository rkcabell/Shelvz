package com.example.shelvz.ui.library

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shelvz.data.model.UserFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelvz.data.model.User
import com.example.shelvz.data.repository.FileRepository
import com.example.shelvz.data.repository.UserRepository
import com.example.shelvz.util.MyResult
import com.example.shelvz.util.extractPdfThumbnail
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    val _fileList = MutableStateFlow<List<UserFile>>(emptyList())
    val fileList: StateFlow<List<UserFile>> = _fileList.asStateFlow()

    private val _thumbnails = MutableStateFlow<Map<String, Bitmap?>>(emptyMap())
    val thumbnails = _thumbnails.asStateFlow()

    // State to handle loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isDeleteMode = MutableStateFlow(false)
    val isDeleteMode: StateFlow<Boolean> = _isDeleteMode.asStateFlow()

    val _selectedFile = MutableStateFlow<UserFile?>(null)
    val selectedFile = _selectedFile.asStateFlow()

    private val _selectedCard = MutableStateFlow<UserFile?>(null)
    val selectedCard = _selectedCard.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

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
    private fun loadLibrary() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true

                // Fetch the latest user
                val user = userRepository.getLoggedInUser().firstOrNull()
                withContext(Dispatchers.Main) {
                    _loggedInUser.value = user
                    _fileList.value = user?.library ?: emptyList()
                    Log.d("LibraryViewModel", "Loaded library: ${_fileList.value.map { it.name }}")
                }
            } catch (e: Exception) {
                Log.e("LibraryViewModel", "Error loading library: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    // Refresh the library
    // delay: allows time for the refreshing state to update the UI
    fun refreshLibrary() {
        if (_isLoading.value) return // Prevent multiple refreshes
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("LibraryViewModel", "isLoading set to true")
                val startTime = System.currentTimeMillis()

                // Perform the actual library loading
                loadLibrary()

                // Ensure the refresh action takes at least 500ms
                val elapsedTime = System.currentTimeMillis() - startTime
                if (elapsedTime < 500) {
                    delay(500 - elapsedTime)
                }
            } finally {
                Log.d("LibraryViewModel", "isLoading set to false")
                _isLoading.value = false
            }
        }
    }

    // ====================
    // File Operations
    // ====================

    // Add a new file to the library
    // Added runBlocking to prevent function from being 'suspend'
    private fun addFile(file: UserFile): MyResult<Unit> {
        viewModelScope.launch {
            try {
                val user = loggedInUser.value

                // Ensure the user is logged in
                if (user == null) {
                    _snackbarMessage.value = "No user is logged in"
                    return@launch
                }

                // Check if the file already exists in the library
                val currentFiles = fileRepository.getFilesByUser(file.userId)
                if (currentFiles.any { it.uri == file.uri }) {
                    _snackbarMessage.value = "File already exists in the library"
                    return@launch
                }

                // Add the file to the repository
                fileRepository.addFile(file)

                // Update the user's library with the new file
                val updatedLibrary = user.library + file
                userRepository.updateUser(user.copy(library = updatedLibrary))

                // Update the logged-in user and file list
                _loggedInUser.value = user.copy(library = updatedLibrary)
                _fileList.value = currentFiles + file

                // Log the success
                Log.d("LibraryViewModel", "File successfully added: ${file.name}")
                Log.d("LibraryViewModel", "Updated user library: ${updatedLibrary.map { it.name }}")
            } catch (e: Exception) {
                Log.e("LibraryViewModel", "Error adding file: ${e.message}", e)
                _snackbarMessage.value = "An error occurred while adding the file"
            }
        }
        return MyResult.Success(Unit) // Always return a result synchronously
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

    fun deleteFile(file: UserFile) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("LibraryViewModel", "Deleting file: ${file.name}")

                // Remove file from the library
                fileRepository.deleteFile(file)
                Log.d("LibraryViewModel", "File successfully removed from repository: ${file.name}")

                // Update the user's library and recentlyOpenedFiles
                loggedInUser.value?.let { user ->
//                    Log.d("LibraryViewModel", "Original user library: ${user.library.map { it.name }}")

                    val updatedLibrary = user.library.filterNot { it.id == file.id }
                    val updatedRecentlyOpened = user.recentlyOpenedFiles.filterNot { it.id == file.id }

                    val updatedUser = user.copy(
                        library = updatedLibrary,
                        recentlyOpenedFiles = updatedRecentlyOpened
                    )

                    userRepository.updateUser(updatedUser)
                    _loggedInUser.value = updatedUser

                    Log.d("LibraryViewModel", "Updated user library: ${updatedLibrary.map { it.name }}")
//                    Log.d(
//                        "LibraryViewModel",
//                        "Updated recently opened files: ${updatedRecentlyOpened.map { it.name }}"
//                    )
                }

                // Clear stale selectedFile and selectedCard
                withContext(Dispatchers.Main) {
                    if (_selectedFile.value?.id == file.id) {
                        Log.d(
                            "LibraryViewModel",
                            "Clearing stale selectedFile: ${_selectedFile.value?.name}"
                        )
                        clearSelectedFile()
                    }
                    if (_selectedCard.value?.id == file.id) {
                        Log.d(
                            "LibraryViewModel",
                            "Clearing stale selectedCard: ${_selectedCard.value?.name}"
                        )
                        clearSelectedCard()
                    }
                }

                // Reload the library to ensure sync
                withContext(Dispatchers.Main) {
                    loadLibrary()
                    _fileList.value = _loggedInUser.value?.library ?: emptyList() // Force sync
                    Log.d(
                        "LibraryViewModel",
                        "Synced _fileList with user.library: ${_fileList.value.map { it.name }}"
                    )
                }
            } catch (e: Exception) {
                Log.e("LibraryViewModel", "Error deleting file: ${e.message}", e)
            }
        }
    }


    // ====================
    // Utility Functions
    // ====================

    fun setDeleteMode(isEnabled: Boolean) {
        _isDeleteMode.value = isEnabled
    }

    fun selectFile(file: UserFile?) {
        _selectedFile.value = file
    }

    fun clearSelectedFile() {
        _selectedFile.value = null
    }

    fun selectCard(file: UserFile?) {
        _selectedCard.value = file
    }

    fun clearSelectedCard() {
        _selectedCard.value = null
    }


    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun calculateCardSize(screenWidth: Dp): Dp {
        return (screenWidth - 32.dp) / 3
    }

    fun loadThumbnail(file: UserFile, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val thumbnail = extractPdfThumbnail(context, file)
            _thumbnails.value = _thumbnails.value.toMutableMap().apply {
                put(file.id.toString(), thumbnail)
            }
        }
    }




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

    fun logFilteredItems(filteredItems: List<UserFile>) {
        Log.d("LibraryViewModel", "Filtered items after deletion: ${filteredItems.map { it.name }}")
    }
}