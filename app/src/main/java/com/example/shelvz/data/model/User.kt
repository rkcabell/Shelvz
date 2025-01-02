package com.example.shelvz.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.shelvz.util.Converters
import java.util.UUID
import java.time.LocalDate

@Entity(tableName = "users")
@TypeConverters(Converters::class)
data class User(
    @PrimaryKey val id: UUID,
    val name: String,
    val dob: LocalDate, //TypeConverter to JSON
    val bio: String,
    val library: List<Media>, //TypeConverter to JSON
    // Map of Media UUID to Ratings | 0.0 to 5.0 in increments of 0.5 where null is unrated
    val ratedMedia: Map<UUID, Float?>,
    val reviewedMedia: Map<UUID, String?>
)