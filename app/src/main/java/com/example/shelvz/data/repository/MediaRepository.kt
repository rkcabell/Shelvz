package com.example.shelvz.data.repository

import com.example.shelvz.data.dao.MediaDao
import com.example.shelvz.data.model.Media
import java.util.UUID
import com.example.shelvz.util.Result
import javax.inject.Inject

class MediaRepository @Inject constructor(private val mediaDao: MediaDao) {
    suspend fun insertMedia(media: Media): Result<Unit> {
        return try {
            mediaDao.insertMedia(media)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getMediaById(mediaId: UUID): Result<Media> {
        return try {
            val media = mediaDao.getMediaById(mediaId)
            Result.Success(media)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteMedia(media: Media): Result<Unit> {
        return try {
            mediaDao.deleteMedia(media)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}