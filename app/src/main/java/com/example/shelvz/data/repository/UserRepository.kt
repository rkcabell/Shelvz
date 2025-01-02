package com.example.shelvz.data.repository
import com.example.shelvz.data.db.UserDao
import com.example.shelvz.data.model.User
import java.util.UUID

/*
Manages data related to users.
Handles data operations and provides data to the ViewModel.
Fetches user details from a remote API or local database.
Example: getUserById(userId: String): User
 */
class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)
    suspend fun updateBio(userId: UUID, newBio: String) = userDao.updateBio(userId, newBio)
    suspend fun getUserBio(userId: UUID) = userDao.getUserById(userId)
    suspend fun getRatedMedia(userId: UUID) = userDao.getRatedMedia(userId)
    suspend fun updateRatedMedia(userId: UUID, updatedRatings: Map<UUID, Float>) = userDao.updateRatedMedia(userId, updatedRatings)
}