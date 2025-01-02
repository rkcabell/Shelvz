package com.example.shelvz.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.shelvz.util.Converters
import java.time.LocalDate
import java.util.UUID

@Entity(tableName = "media")
@TypeConverters(Converters::class)
data class Media(
    @PrimaryKey val mediaId: UUID,
    val title: String,
    val summary: String,
    val genres: List<String>, //TypeConverter to JSON
    val releaseDate: LocalDate,
    val mediaType: MediaType,
    val overallRating: Float
)

enum class MediaType {
    BOOK,
    MOVIE
}