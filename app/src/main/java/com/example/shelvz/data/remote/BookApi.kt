package com.example.shelvz.data.remote

import com.example.shelvz.data.model.BookResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

interface BookApi {

    @GET("subjects/{subject}.json")
    fun getBooksBySubject(
        @Query("subject") subject: String,
        @Query("limit") limit: Int = 20
    ): Call<BookResponse>
}