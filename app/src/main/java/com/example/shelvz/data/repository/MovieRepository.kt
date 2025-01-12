package com.example.shelvz.data.repository

import com.example.shelvz.data.dao.MovieDao
import com.example.shelvz.data.model.Movie
import java.util.UUID
import com.example.shelvz.util.MyResult
import javax.inject.Inject

/*
Manages data related to movies.
Handles data operations and provides data to the ViewModel.
Fetches movie details from a remote API or local database.
 */

class MovieRepository @Inject constructor(private val movieDao: MovieDao) {
    suspend fun insertMovie(movie: Movie): MyResult<Unit> {
        return try {
            movieDao.insertMovie(movie)
            MyResult.Success(Unit)
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }

    suspend fun getMovieById(mediaId: UUID): MyResult<Movie> {
        return try {
            val movie = movieDao.getMovieById(mediaId)
            MyResult.Success(movie)
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }

    suspend fun deleteMovie(movie: Movie): MyResult<Unit> {
        return try {
            movieDao.deleteMovie(movie)
            MyResult.Success(Unit)
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }

    suspend fun getMovieSummaryById(mediaId: UUID): MyResult<String> {
        return try {
            val summary = movieDao.getMovieSummaryById(mediaId)
            MyResult.Success(summary)
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }
    }