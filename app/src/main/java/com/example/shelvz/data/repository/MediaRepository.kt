package com.example.shelvz.data.repository

import com.example.shelvz.data.db.MediaDao
import com.example.shelvz.data.model.Media
import java.util.UUID

class MediaRepository(private val mediaDao: MediaDao) {
    suspend fun insertMedia(media: Media) = mediaDao.insertMedia(media)
    suspend fun getMediaById(mediaId: UUID) = mediaDao.getMediaById(mediaId)
    suspend fun deleteMedia(media: Media) = mediaDao.deleteMedia(media)
}