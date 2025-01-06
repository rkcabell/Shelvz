package com.example.shelvz.data.repository

import com.example.shelvz.data.dao.MovieDao
import com.example.shelvz.data.model.Movie
import java.util.UUID
import com.example.shelvz.util.Result
import javax.inject.Inject

/*
Manages data related to movies.
Handles data operations and provides data to the ViewModel.
Fetches movie details from a remote API or local database.
 */

class MovieRepository @Inject constructor(private val movieDao: MovieDao) {
    suspend fun insertMovie(movie: Movie): Result<Unit> {
        return try {
            movieDao.insertMovie(movie)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getMovieById(mediaId: UUID): Result<Movie> {
        return try {
            val movie = movieDao.getMovieById(mediaId)
            Result.Success(movie)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteMovie(movie: Movie): Result<Unit> {
        return try {
            movieDao.deleteMovie(movie)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getMovieSummaryById(mediaId: UUID): Result<String> {
        return try {
            val summary = movieDao.getMovieSummaryById(mediaId)
            Result.Success(summary)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    }