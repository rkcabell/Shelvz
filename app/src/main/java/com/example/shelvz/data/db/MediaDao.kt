package com.example.shelvz.data.db
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import com.example.shelvz.data.model.Media
import java.util.UUID

@Dao
interface MediaDao {
    @Insert
    suspend fun insertMedia(media: Media): Any

    @Delete
    suspend fun deleteMedia(media: Media): Any

    @Query("SELECT * FROM media WHERE mediaId = :mediaId")
    suspend fun getMediaById(mediaId: UUID): Media
}

