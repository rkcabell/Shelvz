package com.example.shelvz.data.repository

import com.example.shelvz.data.dao.BookDao
import com.example.shelvz.data.model.Book
import com.example.shelvz.data.remote.BookMapper
import com.example.shelvz.data.remote.BookResponse
import java.util.UUID
import com.example.shelvz.util.MyResult
import com.example.shelvz.data.remote.RetrofitClient
import retrofit2.Call
import javax.inject.Inject

/*
    Manages data operations for books
    serves as the bridge between the local database (Room) and the remote API (Retrofit).
 */

class BookRepository @Inject constructor(
    private val bookDao: BookDao,
    private val retrofitClient: RetrofitClient) {

    private val bookApi = retrofitClient.bookApi

    suspend fun fetchBooksBySubject(
        subject: String,
        useLocal: Boolean = true,
        onSuccess: (List<Book>) -> Unit,
        onError: (Throwable) -> Unit
    ) {

        if (useLocal){
            //Fetch from Room
            try {
                val books = bookDao.getBooksBySubject(subject) // Assuming this method exists in BookDao
                onSuccess(books)
            } catch (e: Exception) {
                onError(e)
            }
        }
        else {
            //Fetch from API
            val call = bookApi.getBooksBySubject(subject)

            call.enqueue(object : retrofit2.Callback<BookResponse> {
                override fun onResponse(
                    call: Call<BookResponse>,
                    response: retrofit2.Response<BookResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.works?.let { bookJsonList ->
                            // Process the list of BookJson
                            onSuccess(bookJsonList.map { bookJson ->
                                BookMapper.mapToEntity(bookJson)
                            })
                        }
                    } else {
                        val errorMessage = when (response.code()) {
                            400 -> "Bad Request"
                            404 -> "Data not found"
                            500 -> "Server error"
                            else -> "Unknown error"
                        }
                        onError(Exception(errorMessage))
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