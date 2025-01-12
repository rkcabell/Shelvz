package com.example.shelvz.data.repository
import com.example.shelvz.data.dao.UserDao
import com.example.shelvz.data.datastore.DataStoreManager
import com.example.shelvz.data.model.User
import com.example.shelvz.util.MyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID
import javax.inject.Inject

/*
Manages data related to users.
Handles data operations and provides data to the ViewModel.
Fetches user details from a remote API or local database.
 */
class UserRepository @Inject constructor(private val userDao: UserDao, private val dataStoreManager: DataStoreManager) {

    val isLoggedIn: Flow<Boolean> = dataStoreManager.isLoggedIn
    val loggedInUserId: Flow<UUID?> = dataStoreManager.loggedInUserId

    suspend fun login(name: String, password: String): MyResult<Boolean> {
        println("in userRepository login")
        return try {
            val user = userDao.getUserByName(name)
            if (user != null && BCrypt.checkpw(password, user.password)) {
                userDao.updateLoginStatus(user.id, true)
                setUserLoginStatus(true)
                dataStoreManager.setUserLoggedIn(true, user.id)
                MyResult.Success(true)
            } else {
                println("Invalid username or password")
                MyResult.Error(Exception("Invalid username or password"))
            }
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }

    suspend fun logout() {
        try {
            val user = getLoggedInUser().firstOrNull()
            if (user != null) {
                userDao.updateLoginStatus(user.id, false) // Mark the user as logged out
            }
            dataStoreManager.setUserLoggedIn(false) // Clear login status in DataStore
        } catch (e: Exception) {
            println("Failed to logout: ${e.message}")
        }
    }

    suspend fun setUserLoginStatus(isLoggedIn: Boolean) {
        try {
            val user = getLoggedInUser().firstOrNull()
            if (user != null) {
                userDao.updateLoginStatus(user.id, isLoggedIn)
            } else {
                println("No logged-in user found. Skipping login status update.")
            }
        } catch (e: Exception) {
            println("Failed to update login status: ${e.message}")
        }
    }

    fun getLoggedInUser(): Flow<User?> {
        return userDao.getLoggedInUser()
    }


    suspend fun saveUser(user: User): MyResult<Unit> {
        return try {
            userDao.insertUser(user)
            MyResult.Success(Unit)
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }

    suspend fun getUserByName(name: String): User? {
        return try {
            userDao.getUserByName(name)
        } catch (e: Exception) {
            println("Failed to fetch user by name: ${e.message}")
            null
        }
    }

    suspend fun deleteUser(user: User): MyResult<Unit> {
        return try {
            userDao.deleteUser(user)
            MyResult.Success(Unit)
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }

    suspend fun updateBio(userId: UUID, newBio: String): MyResult<Unit> {
        return try {
            userDao.updateBio(userId, newBio)
            MyResult.Success(Unit)
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }

    suspend fun getUserBio(userId: UUID): MyResult<String> {
        return try {
            val bio = userDao.getUserBioById(userId)
            MyResult.Success(bio)
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }


    suspend fun getUserById(userId: UUID): MyResult<User> {
        return try {
            val user = userDao.getUserById(userId)
            MyResult.Success(user)
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }

    suspend fun getUsername(userId: UUID): MyResult<String> {
        return try {
            val name = userDao.getUsername(userId)
            MyResult.Success(name)
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }

}