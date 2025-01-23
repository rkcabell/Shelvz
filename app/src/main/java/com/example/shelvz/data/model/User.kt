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
    val password: String,
    val profilePic: String? = null, // Path or URL for profile picture
    val dob: LocalDate,
    var bio: String,
    val library: List<UserFile>,
    val favorites: List<UUID>,
    val favoriteGenres: List<String>, //enum?
    val isLoggedIn: Boolean = false,
    val recentlyOpenedFiles: List<UserFile> = emptyList()

)

