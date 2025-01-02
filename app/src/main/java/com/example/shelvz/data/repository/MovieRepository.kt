package com.example.shelvz.data.repository

import com.example.shelvz.data.db.MovieDao
import com.example.shelvz.data.model.Movie
import java.util.UUID

/*
Manages data related to movies.
Handles data operations and provides data to the ViewModel.
Fetches movie details from a remote API or local database.
Example: getPopularMovies(): List<Movie>
 */

class MovieRepository(private val movieDao: MovieDao) {
        suspend fun insertMovie(movie: Movie) = movieDao.insertMovie(movie)
        suspend fun getMovieById(mediaId: UUID) = movieDao.getMovieById(mediaId)
        suspend fun deleteMovie(movie: Movie) = movieDao.deleteMovie(movie)
        suspend fun getMovieSummaryById(mediaId: UUID) = movieDao.getMovieSummaryById(mediaId)
    }