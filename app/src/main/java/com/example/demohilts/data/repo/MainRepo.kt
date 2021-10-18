package com.example.demohilts.data.repo

import com.example.demohilts.base.BaseResponse
import com.example.demohilts.data.entity.*

interface MainRepo {

    suspend fun getPopulars(key: String, page: Int): BaseResponse<List<MovieSummary>>

    suspend fun getTrendings(key: String): BaseResponse<List<MovieSummary>>

    suspend fun getGenres(key: String): Genres

    suspend fun getMovieDetail(id: Int, key: String, type: String): MovieDetail

    suspend fun getCastsAndCrews(id: Int, key: String, type: String): CastsCrews

    suspend fun getSimilars(id: Int, key: String, page: Int, type: String): BaseResponse<List<MovieSummary>>

    suspend fun getReviews(id: Int, key: String, page: Int, type: String): BaseResponse<List<Comment>>

    suspend fun getVideos(id: Int, key: String, type: String): BaseResponse<List<Video>>

    suspend fun getGenreMovies(key: String, page: Int, genreId: Int): BaseResponse<List<MovieSummary>>

    suspend fun searchMovies(key: String, page: Int, query: String): BaseResponse<List<MovieSummary>>

    suspend fun searchKW(key: String, page: Int, query: String): BaseResponse<List<KeyWord>>

    suspend fun getMovieImages(id: Int, key: String, type: String): Images

    suspend fun getMovieKeyWords(id: Int, key: String, type: String): KeyWords
}
