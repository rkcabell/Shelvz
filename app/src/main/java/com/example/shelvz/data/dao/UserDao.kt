package com.example.shelvz.data.dao

import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.shelvz.data.model.Review
import com.example.shelvz.data.model.User
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface UserDao {
    @Query("SELECT bio FROM users WHERE id = :userId")
    suspend fun getUserBioById(userId: UUID): String

    @Query("UPDATE users SET bio = :newBio WHERE id = :userId")
    suspend fun updateBio(userId: UUID, newBio: String)

    @Query("SELECT * FROM users WHERE name = :name LIMIT 1")
    suspend fun getUserByName(name: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT name FROM users WHERE id = :userId")
    suspend fun getUsername(userId: UUID): String

    @Query("SELECT * FROM users WHERE name = :name AND password = :password")
    suspend fun getUserByLogin(name: String, password: String): User?

    @Query("SELECT * FROM users WHERE isLoggedIn IS 1 LIMIT 1 ")
    fun getLoggedInUser(): Flow<User?>

    @Query("UPDATE users SET isLoggedIn = :isLoggedIn WHERE id = :userId")
    suspend fun updateLoginStatus(userId: UUID, isLoggedIn: Boolean)

    @Query("DELETE FROM users")
    suspend fun clearAllUsers()

    //CRUD

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: UUID): User

    @Delete
    suspend fun deleteUser(user: User)



}
