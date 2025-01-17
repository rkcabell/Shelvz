package com.example.shelvz.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shelvz.data.model.File
import java.util.UUID

@Dao
interface FileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: File)

    @Query("SELECT * FROM files WHERE userId = :userId")
    suspend fun getFilesByUser(userId: UUID): List<File>

    @Query("DELETE FROM files WHERE id = :fileId")
    suspend fun deleteFile(fileId: UUID)

    @Query("SELECT * FROM files")
    suspend fun getAllFiles(): List<File>
}