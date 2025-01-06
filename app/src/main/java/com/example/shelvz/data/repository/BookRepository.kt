package com.example.shelvz.data.repository

import com.example.shelvz.data.dao.BookDao
import com.example.shelvz.data.model.Book
import java.util.UUID
import com.example.shelvz.util.Result
import javax.inject.Inject

/*
Manages data related to books.
Handles data operations and provides data to the ViewModel.
Fetches book details from a remote API or local database.
 */

class BookRepository @Inject constructor(private val bookDao: BookDao) {
    suspend fun insertBook(book: Book): Result<Unit> {
        return try {
            bookDao.insertBook(book)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getBookById(mediaId: UUID): Result<Book> {
        return try {
            val book = bookDao.getBookById(mediaId)
            Result.Success(book)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteBook(book: Book): Result<Unit> {
        return try {
            bookDao.deleteBook(book)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getBookSummaryById(mediaId: UUID): Result<String> {
        return try {
            val summary = bookDao.getBookSummaryById(mediaId)
            Result.Success(summary)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}