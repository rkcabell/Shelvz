package com.example.shelvz.data.repository

import com.example.shelvz.data.db.BookDao
import com.example.shelvz.data.model.Book
import java.util.UUID

/*
Manages data related to books.
Handles data operations and provides data to the ViewModel.
Fetches book details from a remote API or local database.
Example: getBookDetails(bookId: String): Book
 */

class BookRepository(private val bookDao: BookDao) {
    suspend fun insertBook(book: Book) = bookDao.insertBook(book)
    suspend fun getBookById(mediaId: UUID) = bookDao.getBookById(mediaId)
    suspend fun deleteBook(book: Book) = bookDao.deleteBook(book)
    suspend fun getBookSummaryById(mediaId: UUID) = bookDao.getBookSummaryById(mediaId)

}