package com.example.shelvz.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index
import androidx.room.TypeConverters
import java.util.UUID

@Entity(
    tableName = "reviews",
    indices = [Index(value = ["mediaId"])],
    foreignKeys = [
        ForeignKey(
            entity = Media::class,
            parentColumns = ["mediaId"],
            childColumns = ["mediaId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Review(
    @PrimaryKey val reviewId: UUID,
    val mediaId: UUID,
    val reviewerId: UUID,
    val rating: Float,

    @ColumnInfo(defaultValue = "")
    val reviewText: String
)
