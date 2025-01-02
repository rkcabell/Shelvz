package com.example.shelvz.data.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import com.example.shelvz.data.model.Movie
import com.example.shelvz.data.model.User
import java.util.UUID

@Dao
interface MovieDao {


    @Insert
    suspend fun insertMovie(movie: Movie): Any

    @Delete
    suspend fun deleteMovie(movie: Movie): Any

    @Query("SELECT * FROM movies WHERE mediaId = :mediaId")
    suspend fun getMovieById(mediaId: UUID): Movie

    @Query("SELECT m.summary \n" +
            "    FROM Media m \n" +
            "    INNER JOIN movies b ON m.mediaId = b.mediaId\n" +
            "    WHERE b.mediaId = :mediaId")
    suspend fun getMovieSummaryById(mediaId: UUID): String
}

