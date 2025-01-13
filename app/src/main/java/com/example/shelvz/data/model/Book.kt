package com.example.shelvz.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import com.google.gson.annotations.SerializedName

@Entity(tableName = "books", foreignKeys = [
    ForeignKey(entity = Media::class,
        parentColumns = ["mediaId"],
        childColumns = ["mediaId"],
        onDelete = CASCADE)
])
data class Book(
    @PrimaryKey @SerializedName("media_id") val mediaId: UUID,
    @SerializedName("author") val author: String,
    @SerializedName("isbn") val isbn: String,
    @SerializedName("publisher") val publisher: String,
    @SerializedName("page_count") val pageCount: Int,
    @SerializedName("edition") val edition: Int
) {
//    val coverUrl: String
//        get() = "https://covers.openlibrary.org/b/id/$coverId-L.jpg"
}

data class BookResponse(
    @SerializedName("works") val works: List<Book>
)


data class BookDto(
    @SerializedName("title") val title: String,
    @SerializedName("cover_id") val coverId: Int?,
    @SerializedName("author_name") val authors: List<String>?
) {

    fun BookDto.toEntity(): Book {
        return Book(
            mediaId = UUID.randomUUID(), // Generate if not provided
            author = authors?.joinToString(", ") ?: "Unknown",
            isbn = "", // Populate if available
            publisher = "", // Populate if available
            pageCount = 0, // Populate if available
            edition = 1, // Default value
        )
    }
}