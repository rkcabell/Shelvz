package com.example.shelvz.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(tableName = "books", foreignKeys = [ForeignKey(entity = Media::class, parentColumns = ["mediaId"], childColumns = ["mediaId"], onDelete = CASCADE)])
data class Book(
    @PrimaryKey val mediaId: UUID,
    val author: String
)