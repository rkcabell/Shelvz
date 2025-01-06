package com.example.shelvz.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shelvz.data.model.Review
import java.util.UUID

@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review): Long

    @Delete
    suspend fun deleteReview(review: Review)

    @Query("SELECT * FROM reviews WHERE mediaId = :mediaId")
    suspend fun getReviewsForMedia(mediaId: UUID): Review

    @Query("SELECT * FROM reviews WHERE reviewerId = :reviewerId")
    suspend fun getReviewsByUser(reviewerId: UUID): Review


}