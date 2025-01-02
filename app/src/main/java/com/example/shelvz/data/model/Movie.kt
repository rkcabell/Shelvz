package com.example.shelvz.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.TypeConverters
import com.example.shelvz.util.Converters

@Entity(tableName = "movies", foreignKeys = [ForeignKey(entity = Media::class, parentColumns = ["mediaId"], childColumns = ["mediaId"], onDelete = CASCADE)])
@TypeConverters(Converters::class)
data class Movie(
    @PrimaryKey val mediaId: UUID, // One-to-one relationship with Media
    val director: String,
    val cast: List<String> //TypeConverter to JSON
)