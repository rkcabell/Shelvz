package com.example.shelvz.data.model

import com.example.shelvz.util.Converters
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.UUID
import java.time.LocalDate

@Entity(tableName = "users")
@TypeConverters(Converters::class)
data class User(
    @PrimaryKey val id: UUID,
    val name: String,
    val email: String,
    val password: String, // Store hashed password or token
    val profilePic: String? = null, // Path or URL for profile picture
    val dob: LocalDate,
    val bio: String,
    val library: List<String>,              // Paths to media files on device
    //val reviewCount: Int,                 // Calculated at runtime or stored as derived column in database
    val favorites: List<UUID>,
    val favoriteGenres: List<String> //enum?
)

