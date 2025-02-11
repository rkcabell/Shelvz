package com.example.shelvz.data.remote

import androidx.room.PrimaryKey
import com.example.shelvz.data.model.Book
import com.example.shelvz.data.model.MediaDetails
import com.example.shelvz.data.model.MediaType
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.http.Path
import java.time.LocalDate
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
    @SerializedName("title") val title: String,
    @SerializedName("isbn") val isbn: String,
    @SerializedName("publisher") val publisher: String,
    @SerializedName("page_count") val pageCount: Int,
    @SerializedName("edition") val edition: Int,
    @SerializedName("subject") val subject: String,
    @SerializedName("summary") val summary: String?,
    @SerializedName("release_date") val releaseDate: LocalDate?,
    @SerializedName("average_rating") val averageRating: Float?,
    @SerializedName("thumbnail_path") val thumbnailPath: String?
)

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
            mediaId = json.mediaId,
            title = json.title,
            author = json.author,
            isbn = json.isbn,
            publisher = json.publisher,
            pageCount = json.pageCount,
            edition = json.edition,
            subject = json.subject,
            media = MediaDetails(
                title = json.title, // Still map title to MediaDetails for consistency
                summary = "",
                releaseDate = LocalDate.now(),
                mediaType = MediaType.BOOK,
                averageRating = 0.0f,
                thumbnailPath = ""
            )
        )
    }
}