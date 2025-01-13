package com.example.shelvz

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.shelvz.data.remote.BookApi
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(AndroidJUnit4::class)
class BookApiTest {

    @Test
    fun fetchBookDetails() {
        runBlocking {
            // Retrofit client setup
            val retrofit = Retrofit.Builder()
                .baseUrl("https://openlibrary.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val bookApi = retrofit.create(BookApi::class.java)

            // Predefined list of subjects
            val predefinedSubjects = listOf(
                "Arts", "Animals", "Fiction", "Science & Mathematics", "Business & Finance",
                "Children's", "History", "Health & Wellness", "Biography", "Social Sciences",
                "Places", "Textbooks"
            )

            // Make the API call
            try {
                val response = bookApi.getBookDetails("OL29983W").execute() // Use synchronous call
                if (response.isSuccessful) {
                    val bookDetails = response.body()
                    val title = bookDetails?.title ?: "Unknown Title"
                    val subjects = bookDetails?.subjects ?: emptyList()

                    // Find the first matching subject
                    val matchingSubject = subjects.firstOrNull { it in predefinedSubjects }

                    Log.d("Test", "Title: $title")
                    Log.d("Test", "Subject: ${matchingSubject ?: "No matching subject found"}")
                } else {
                    Log.d("Test", "Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.d("Test", "API call failed: ${e.message}")
            }
        }
    }
}