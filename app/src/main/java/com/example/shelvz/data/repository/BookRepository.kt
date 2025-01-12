package com.example.shelvz.data.repository

import com.example.shelvz.data.dao.BookDao
import com.example.shelvz.data.model.Book
import java.util.UUID
import com.example.shelvz.util.MyResult
import javax.inject.Inject

/*
Manages data related to books.
Handles data operations and provides data to the ViewModel.
Fetches book details from a remote API or local database.
 */

class BookRepository @Inject constructor(private val bookDao: BookDao) {
    suspend fun insertBook(book: Book): MyResult<Unit> {
        return try {
            bookDao.insertBook(book)
            MyResult.Success(Unit)
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }

    suspend fun getBookById(mediaId: UUID): MyResult<Book> {
        return try {
            val book = bookDao.getBookById(mediaId)
            MyResult.Success(book)
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }

    suspend fun deleteBook(book: Book): MyResult<Unit> {
        return try {
            bookDao.deleteBook(book)
            MyResult.Success(Unit)
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }

    suspend fun getBookSummaryById(mediaId: UUID): MyResult<String> {
        return try {
            val summary = bookDao.getBookSummaryById(mediaId)
            MyResult.Success(summary)
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }

}