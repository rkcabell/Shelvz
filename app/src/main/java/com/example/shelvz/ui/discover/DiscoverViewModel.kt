package com.example.shelvz.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelvz.data.model.Book
import com.example.shelvz.data.model.MediaDetails
import com.example.shelvz.data.model.MediaType
import com.example.shelvz.data.repository.BookRepository
import com.example.shelvz.util.MyResult
import com.example.shelvz.util.Subjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

sealed class DiscoverUiState {
    data object Loading : DiscoverUiState()
    data class Success(val books: List<Book>) : DiscoverUiState()
    data class Error(val message: String) : DiscoverUiState()
}


// Manages data flow and transforms repository results into UI-specific states.

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    // BEGIN DUMMY DATA

    private val booksBySubject = Subjects.primarySubjects.associateWith { subject ->
        List(10) { index ->
            Book(
                mediaId = UUID.randomUUID(),
                author = "Author $index",
                isbn = "123456789$index",
                publisher = "Publisher $index",
                pageCount = 100 + index,
                edition = 1,
                subject = Subjects.resolveSubject(subject),
                title = "$subject Book $index",
                media = MediaDetails( // Provide dummy data for MediaDetails
                    title = "$subject Book $index",
                    summary = "This is a summary for $subject Book $index.",
                    releaseDate = LocalDate.now(),
                    mediaType = MediaType.BOOK,
                    averageRating = (3.0 + index % 2).toFloat(),
                    thumbnailPath = "/dummy/path/to/thumbnail$index.png"
                )
            )
        }
    }

    private val _uiState = MutableStateFlow<DiscoverUiState>(
        DiscoverUiState.Success(booksBySubject.values.flatten())
    )

    // END DUMMY DATA

    // Real _uiState not using dummy data
//    private val _uiState = MutableStateFlow<DiscoverUiState>(DiscoverUiState.Loading)
    val uiState: StateFlow<DiscoverUiState> get() = _uiState

    fun fetchBooksBySubject(subject: String) {
        _uiState.value = DiscoverUiState.Loading

        viewModelScope.launch {
            try {
                bookRepository.fetchBooksBySubject(
                    subject = subject,
                    useLocal = false, // For now, always fetch from API
                    onSuccess = { books ->
                        _uiState.value = DiscoverUiState.Success(books)
                    },
                    onError = { throwable ->
                        _uiState.value = DiscoverUiState.Error(throwable.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = DiscoverUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun searchBooks(query: String) {
        val currentState = _uiState.value
        if (currentState is DiscoverUiState.Success) {
            val filteredBooks = currentState.books.filter {
                it.title.contains(query, ignoreCase = true)
            }
            _uiState.value = DiscoverUiState.Success(filteredBooks)
        }
    }
}
