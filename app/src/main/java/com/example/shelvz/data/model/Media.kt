package com.example.shelvz.data.model

import com.example.shelvz.util.Converters
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDate
import java.util.UUID
import javax.security.auth.Subject

@Entity(tableName = "media")
@TypeConverters(Converters::class)
data class Media(
    @PrimaryKey val mediaId: UUID,
    val title: String,
    val summary: String,
    val subject: String,
    val releaseDate: LocalDate,
    val mediaType: MediaType,
    val averageRating: Float,
    val thumbnailPath: String
)

enum class MediaType {
    BOOK,
    MOVIE
}