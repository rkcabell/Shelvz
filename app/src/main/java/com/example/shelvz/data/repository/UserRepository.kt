package com.example.shelvz.data.repository
import com.example.shelvz.data.db.UserDao

/*
Manages data related to users.
Handles data operations and provides data to the ViewModel.
Fetches user details from a remote API or local database.
Example: getUserById(userId: String): User
 */
class UserRepository(private val userDao: UserDao) {

    suspend fun updateBio(userId: Int, newBio: String) {
        userDao.updateBio(userId, newBio)
    }
}