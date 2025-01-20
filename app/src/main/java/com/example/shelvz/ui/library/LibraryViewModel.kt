package com.example.shelvz.ui.library

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.ui.platform.LocalContext
import com.example.shelvz.data.model.UserFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelvz.data.model.User
import com.example.shelvz.data.repository.FileRepository
import com.example.shelvz.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

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

    // State for the currently opened publication
//    private val _currentPublication = MutableStateFlow<Publication?>(null)
//    val currentPublication: StateFlow<Publication?> = _currentPublication.asStateFlow()
//
//    private val epubParser = EpubParser()
//    private val pdfParser = createPdfParser(context)

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
                _fileList.value = user?.let { fileRepository.getFilesByUser(it.id) } ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace() // Log or handle the exception as needed
                _fileList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Add a new file to the library
    fun addFile(file: UserFile) {
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
                _loggedInUser.value?.id?.let { userId ->
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
