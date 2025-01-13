package com.example.shelvz.data.repository

import com.example.shelvz.data.dao.BookDao
import com.example.shelvz.data.model.Book
import com.example.shelvz.data.model.BookResponse
import java.util.UUID
import com.example.shelvz.util.MyResult
import com.example.shelvz.data.remote.RetrofitClient
import retrofit2.Call
import javax.inject.Inject

/*
Manages data related to books.
Handles data operations and provides data to the ViewModel.
Fetches book details from a remote API or local database.
 */

class BookRepository @Inject constructor(private val bookDao: BookDao) {

    private val bookApi = RetrofitClient.bookApi

    fun fetchBooksBySubject(subject: String, useLocal: Boolean = true, onSuccess: (List<Book>) -> Unit, onError: (Throwable) -> Unit) {
        if (useLocal){
            //Fetch from Room
        }
        else {
            //Fetch from API
            val call = bookApi.getBooksBySubject(subject)

            call.enqueue(object : retrofit2.Callback<BookResponse> {
                override fun onResponse(
                    call: Call<BookResponse>,
                    response: retrofit2.Response<BookResponse>
                ) {
                    when (response.code()) {
                        400 -> onError(Exception("Bad Request"))
                        404 -> onError(Exception("Data not found"))
                        500 -> onError(Exception("Server error"))
                        else -> onError(Exception("Unknown error"))
                    }
                    if (response.isSuccessful) {
                        response.body()?.works?.let(onSuccess)
                    } else {
                        onError(Exception("Error: ${response.code()}"))
                    }
                }

                override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                    onError(t)
                }
            })
        }

    }

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