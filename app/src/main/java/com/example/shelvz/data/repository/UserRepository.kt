package com.example.shelvz.data.repository
import com.example.shelvz.data.dao.UserDao
import com.example.shelvz.data.model.User
import com.example.shelvz.util.Result
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID
import javax.inject.Inject

/*
Manages data related to users.
Handles data operations and provides data to the ViewModel.
Fetches user details from a remote API or local database.
 */
class UserRepository @Inject constructor(private val userDao: UserDao) {
    suspend fun insertUser(user: User): Result<Unit> {
        return try {
            userDao.insertUser(user)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteUser(user: User): Result<Unit> {
        return try {
            userDao.deleteUser(user)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateBio(userId: UUID, newBio: String): Result<Unit> {
        return try {
            userDao.updateBio(userId, newBio)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getUserBio(userId: UUID): Result<String> {
        return try {
            val bio = userDao.getUserBioById(userId)
            Result.Success(bio)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    suspend fun getUserById(userId: UUID): Result<User> {
        return try {
            val user = userDao.getUserById(userId)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getUsername(userId: UUID): Result<String> {
        return try {
            val name = userDao.getUsername(userId)
            Result.Success(name)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun login(name: String, password: String): com.example.shelvz.util.Result<User> {
        return try {
            val user = userDao.getUserByName(name)
            if (user != null && BCrypt.checkpw(password, user.password)) {
                com.example.shelvz.util.Result.Success(user)
            } else {
                com.example.shelvz.util.Result.Error(Exception("Invalid username or password"))
            }
        } catch (e: Exception) {
            com.example.shelvz.util.Result.Error(e)
        }
    }

}