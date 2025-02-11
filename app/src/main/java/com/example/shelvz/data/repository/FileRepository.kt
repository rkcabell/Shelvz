package com.example.shelvz.data.repository

import android.util.Log
import android.webkit.MimeTypeMap
import com.example.shelvz.data.dao.FileDao
import com.example.shelvz.data.model.UserFile
import java.io.File
import java.util.UUID
import javax.inject.Inject

class FileRepository @Inject constructor(private val fileDao: FileDao) {

    suspend fun addFile(file: UserFile) {
        fileDao.insertFile(file)
    }

    suspend fun getFilesByUser(userId: UUID): List<UserFile> {
        return fileDao.getFilesByUser(userId)
    }

    suspend fun deleteFile(file: UserFile) {
        fileDao.deleteFile(file.id)
        val remainingFiles = fileDao.getAllFiles()
        Log.d("FileRepository", "Remaining files in database: ${remainingFiles.map { it.name }}")
    }

    suspend fun getAllFiles(): List<UserFile> {
        return fileDao.getAllFiles()
    }

    fun getMimeType(uri: String): String {
        return MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(File(uri).extension)
            ?: "application/octet-stream"
    }

}
