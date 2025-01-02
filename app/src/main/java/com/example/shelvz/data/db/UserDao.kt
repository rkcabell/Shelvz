package com.example.shelvz.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.shelvz.data.model.User
import java.util.UUID

@Dao
interface UserDao {
    @Query("SELECT bio FROM users WHERE id = :userId")
    suspend fun getUserBio(userId: UUID): String

    @Query("UPDATE users SET bio = :newBio WHERE id = :userId")
    suspend fun updateBio(userId: UUID, newBio: String)

    @Query("SELECT ratedMedia FROM users WHERE id = :userId")
    suspend fun getRatedMedia(userId: UUID): Map<UUID, Float>

    @Query("UPDATE users SET ratedMedia = :updatedRatings WHERE id = :userId")
    suspend fun updateRatedMedia(userId: UUID, updatedRatings: Map<UUID, Float>)

    //CRUD

    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: UUID): User

    @Delete
    suspend fun deleteUser(user: User)
}
