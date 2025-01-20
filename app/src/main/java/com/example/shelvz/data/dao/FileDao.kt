package com.example.shelvz.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shelvz.data.model.UserFile
import java.util.UUID

@Dao
interface FileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: UserFile)

    @Query("SELECT * FROM userFiles WHERE userId = :userId")
    suspend fun getFilesByUser(userId: UUID): List<UserFile>

    @Query("DELETE FROM userFiles WHERE id = :fileId")
    suspend fun deleteFile(fileId: UUID)

    @Query("SELECT * FROM userFiles")
    suspend fun getAllFiles(): List<UserFile>
}