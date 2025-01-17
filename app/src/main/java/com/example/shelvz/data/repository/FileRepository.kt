package com.example.shelvz.data.repository

import com.example.shelvz.data.dao.FileDao
import com.example.shelvz.data.model.File
import java.util.UUID
import javax.inject.Inject

class FileRepository @Inject constructor(private val fileDao: FileDao) {

    suspend fun addFile(file: File) {
        fileDao.insertFile(file)
    }

    suspend fun getFilesByUser(userId: UUID): List<File> {
        return fileDao.getFilesByUser(userId)
    }

    suspend fun deleteFile(fileId: UUID) {
        fileDao.deleteFile(fileId)
    }

    suspend fun getAllFiles(): List<File> {
        return fileDao.getAllFiles()
    }
}
