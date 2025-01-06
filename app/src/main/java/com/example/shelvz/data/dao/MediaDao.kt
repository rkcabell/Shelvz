package com.example.shelvz.data.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shelvz.data.model.Media
import java.util.UUID

@Dao
interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: Media): Long

    @Delete
    suspend fun deleteMedia(media: Media)

    @Query("SELECT * FROM media WHERE mediaId = :mediaId")
    suspend fun getMediaById(mediaId: UUID): Media
}

