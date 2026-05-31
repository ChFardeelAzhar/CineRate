package com.example.cinerate.data.repository

import com.example.cinerate.data.model.Movie
import com.example.cinerate.data.model.Review

interface MovieRepository {
    suspend fun getAllMovies(): List<Movie>
    suspend fun getMovieById(id: Int): Movie?
    suspend fun getReviewsForMovie(movieId: Int): List<Review>
    suspend fun getGenres(): List<String>
    suspend fun searchMovies(query: String): List<Movie>
    suspend fun getMoviesByGenre(genre: String): List<Movie>
}
