package com.example.shelvz.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shelvz.data.model.Movie
import java.util.UUID

@Dao
interface MovieDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie): Long

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("SELECT * FROM movies WHERE mediaId = :mediaId")
    suspend fun getMovieById(mediaId: UUID): Movie

    @Query("SELECT m.summary \n" +
            "    FROM Media m \n" +
            "    INNER JOIN movies b ON m.mediaId = b.mediaId\n" +
            "    WHERE b.mediaId = :mediaId")
    suspend fun getMovieSummaryById(mediaId: UUID): String
}

