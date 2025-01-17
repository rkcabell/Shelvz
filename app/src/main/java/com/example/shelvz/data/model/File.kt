package com.example.shelvz.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "files",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = CASCADE
        )
    ]
)
data class File(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val uri: String,
    val name: String,
    val type: String, // MIME type (e.g., application/epub+zip, application/pdf)
    val size: Long,
    val dateAdded: Long = System.currentTimeMillis()
)
