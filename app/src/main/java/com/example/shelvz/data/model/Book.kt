package com.example.shelvz.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

@Entity(tableName = "books", foreignKeys = [
    ForeignKey(
        entity = Media::class,
        parentColumns = ["mediaId"],
        childColumns = ["mediaId"],
        onDelete = CASCADE)
])
data class Book(
    @PrimaryKey val mediaId: UUID,
    @ColumnInfo(name = "bookTitle") val title: String,
    val author: String,
    val isbn: String,
    val publisher: String,
    val pageCount: Int,
    val edition: Int,
    val subject: String,
    @Embedded val media: MediaDetails
)

data class MediaDetails(
    @ColumnInfo(name = "mediaTitle") val title: String,
    val summary: String?,
    val releaseDate: LocalDate?,
    val mediaType: MediaType,
    val averageRating: Float?,
    val thumbnailPath: String?
)