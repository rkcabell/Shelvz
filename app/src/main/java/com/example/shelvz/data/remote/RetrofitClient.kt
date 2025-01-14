package com.example.shelvz.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitClient @Inject constructor() {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val bookApi: BookApi by lazy {
        retrofit.create(BookApi::class.java)
    }

    companion object {
        private const val BASE_URL = "https://openlibrary.org/"
    }
}