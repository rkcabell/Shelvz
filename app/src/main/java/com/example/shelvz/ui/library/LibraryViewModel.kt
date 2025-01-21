package com.example.shelvz.ui.library

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.example.shelvz.data.model.UserFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelvz.data.model.User
import com.example.shelvz.data.repository.FileRepository
import com.example.shelvz.data.repository.UserRepository
import com.example.shelvz.util.MyResult
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

    private fun loadLibrary() {
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

    fun loadAllFiles() {
        viewModelScope.launch {
            val allFiles = fileRepository.getAllFiles()
            _fileList.value = allFiles.distinctBy { it.id }
        }
    }

    private fun removeDuplicateFiles(files: List<UserFile>): List<UserFile> {
        return files.distinctBy { it.uri }
    }

    // Add a new file to the library
    // Added runBlocking to prevent function from being 'suspend'
     fun addFile(file: UserFile): MyResult<Unit> {
            return try {
                val currentFiles = runBlocking { fileRepository.getFilesByUser(file.userId) }

                if (currentFiles.any { it.uri == file.uri }) {
                    return MyResult.Error(Exception("File already in Library"))
                }

                runBlocking { fileRepository.addFile(file) }
                _fileList.value = currentFiles + file
                MyResult.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                MyResult.Error(e)
            }
    }

    // Delete a file from the library
    fun deleteFile(fileId: UUID) {
        viewModelScope.launch {
            try {
                fileRepository.deleteFile(fileId)
                _loggedInUser.value?.id?.let { userId ->
                    _fileList.value = fileRepository.getFilesByUser(userId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Refresh the library
    // delay: allows time for the refreshing state to update the UI
    suspend fun refreshLibrary() {
        delay(1000)
        loadLibrary()
        Log.d("RefreshIndicator", "Finished refreshing library")
    }

    fun updateFileList(newFiles: List<UserFile>) {
        _fileList.value = mergeAndDeduplicate(_fileList.value, newFiles)
    }

    private fun mergeAndDeduplicate(existingFiles: List<UserFile>, newFiles: List<UserFile>): List<UserFile> {
        val combined = (existingFiles + newFiles)
        return combined.distinctBy { it.id }
    }

    // Open a file with Readium and set the current publication
//    fun openFile(file: UserFile) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val publication = when (file.type) {
//                    "application/epub+zip" -> openEpub(file, epubParser)
//                    "application/pdf" -> openPdf(file, pdfParser)
//                    else -> null
//                }
//                _currentPublication.value = publication
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    private val publicationOpener by lazy {
//        PublicationOpener(
//            parsers = listOf(epubParser, pdfParser),
//            contentProtections = emptyList(),
//            onCreatePublication = { publication, _ -> publication }
//        )
//    }
//
//    private suspend fun openEpub(file: UserFile): Publication? {
//        val epubFile = File(file.uri)
//        val result = publicationOpener.open(epubFile)
//        return if (result is Try.Success) result.result else null
//    }
//
//        val result = publicationOpener.open(fetcher)
//        return if (result is Try.Success) result.result else null
//    }
//
//    private suspend fun openPdf(file: UserFile, pdfParser: PdfParser): Publication? {
//        val pdfFile = File(file.uri)
//        val fetcher = FileFetcher(pdfFile)
//        val publicationOpener = PublicationOpener(
//            parsers = listOf(pdfParser),
//            contentProtections = emptyList(),
//            onCreatePublication = { publication, _ -> publication }
//        )
//
//        val result = publicationOpener.open(fetcher)
//        return if (result is Try.Success) result.result else null
//    }
//
//    private fun createPdfParser(context: Context): PdfParser {
//        val pdfFactory = object : PdfDocumentFactory<PdfiumDocument> {
//            override fun create(link: Link, file: java.io.File): PdfiumDocument {
//                return PdfiumDocument(
//                    context = context,
//                    file = file
//                )
//            }
//        }
//        return PdfParser(
//            context = context,
//            pdfFactory = pdfFactory
//        )
//    }


    fun saveFileToLocalStorage(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val storageDir = File(context.filesDir, "bookshelf")
        if (!storageDir.exists()) storageDir.mkdirs()

        val destinationFile = File(storageDir, uri.lastPathSegment ?: "unknown_file")
        inputStream?.use { input ->
            destinationFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return destinationFile
    }
}
    //Readium open file
//    fun openFile(uri: Uri, contentResolver: ContentResolver) {
//        viewModelScope.launch {
//            try {
//                val publication = Publication.open(uri, contentResolver).getOrNull()
//                if (publication != null) {
//                    // Pass the publication to the Reader screen
//                    _currentPublication.value = publication
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//}
