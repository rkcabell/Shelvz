package com.example.shelvz.data.remote

import androidx.room.PrimaryKey
import com.example.shelvz.data.model.Book
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.http.Path
import java.util.UUID

interface BookApi {

    @GET("subjects/{subject}.json")
    fun getBooksBySubject(
        @Path("subject") subject: String,
        @Query("limit") limit: Int = 20
    ): Call<BookResponse>

    @GET("works/{workId}.json")
    fun getBookDetails(@Path("workId") workId: String): Call<BookDetailsResponse>
}

data class BookJson(
    @PrimaryKey @SerializedName("media_id") val mediaId: UUID,
    @SerializedName("author") val author: String,
    @SerializedName("isbn") val isbn: String,
    @SerializedName("publisher") val publisher: String,
    @SerializedName("page_count") val pageCount: Int,
    @SerializedName("edition") val edition: Int,
    @SerializedName("subject") val subject: String
) {
//    val coverUrl: String
//        get() = "https://covers.openlibrary.org/b/id/$coverId-L.jpg"
}

data class BookResponse(
    @SerializedName("works") val works: List<BookJson>
)


data class BookDetailsResponse(
    @SerializedName("title") val title: String,
    @SerializedName("subjects") val subjects: List<String>?
)

// Data Transfer Object
data class BookDto(
    @SerializedName("title") val title: String,
    @SerializedName("cover_id") val coverId: Int?,
    @SerializedName("author_name") val authors: List<String>?
)

object BookMapper {
    fun mapToEntity(json: BookJson): Book {
        return Book(
            mediaId = UUID.randomUUID(), // Directly use the UUID from BookJson
            author = json.author,
            isbn = json.isbn,
            publisher = json.publisher,
            pageCount = json.pageCount,
            edition = json.edition,
            subject = json.subject
        )
    }
}