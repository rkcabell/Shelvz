package com.example.shelvz.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import com.google.gson.annotations.SerializedName

@Entity(tableName = "books", foreignKeys = [
    ForeignKey(
        entity = Media::class,
        parentColumns = ["mediaId"],
        childColumns = ["mediaId"],
        onDelete = CASCADE)
])
data class Book (
    @PrimaryKey val mediaId: UUID,
    val author: String,
    val title: String,
    val isbn: String,
    val publisher: String,
    val pageCount: Int,
    val edition: Int,
    val subject: String
)