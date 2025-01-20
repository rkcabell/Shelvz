package com.example.shelvz.data.repository

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

    suspend fun deleteFile(fileId: UUID) {
        fileDao.deleteFile(fileId)
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
