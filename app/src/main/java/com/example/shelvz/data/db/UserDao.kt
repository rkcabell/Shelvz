package com.example.shelvz.data.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT bio FROM users WHERE id = :userId")
    suspend fun getUserBio(userId: Int): String

    @Query("UPDATE users SET bio = :newBio WHERE id = :userId")
    suspend fun updateBio(userId: Int, newBio: String)
}