package com.example.shelvz.data.repository

import com.example.shelvz.data.dao.MediaDao
import com.example.shelvz.data.model.Media
import java.util.UUID
import com.example.shelvz.util.MyResult
import javax.inject.Inject

class MediaRepository @Inject constructor(private val mediaDao: MediaDao) {
    suspend fun insertMedia(media: Media): MyResult<Unit> {
        return try {
            mediaDao.insertMedia(media)
            MyResult.Success(Unit)
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }

    suspend fun getMediaById(mediaId: UUID): MyResult<Media> {
        return try {
            val media = mediaDao.getMediaById(mediaId)
            MyResult.Success(media)
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }

    suspend fun deleteMedia(media: Media): MyResult<Unit> {
        return try {
            mediaDao.deleteMedia(media)
            MyResult.Success(Unit)
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }
}