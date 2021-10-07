package com.example.demohilts.data.service

import com.example.demohilts.base.BaseResponse
import com.example.demohilts.data.entity.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("discover/movie")
    suspend fun getDiscover(
        @Query("language") language: String = "en-US",
        @Query("sort_by") sort: String = "popularity.desc",
        @Query("include_adult") adult: Boolean = true,
        @Query("include_video") video: Boolean = true,
        @Query("with_watch_monetization_types") watchType: String = "flatrate",
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
    ): BaseResponse<List<MovieSummary>>

    @GET("trending/all/day")
    suspend fun getTrendingDay(
        @Query("api_key") apiKey: String
    ): BaseResponse<List<MovieSummary>>

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
    ): Genres

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") id: Int,
        @Query("api_key") key: String,
    ): MovieDetail

    @GET("movie/{movie_id}/credits")
    suspend fun getCastsAndCrews(
        @Path("movie_id") id: Int,
        @Query("api_key") key: String,
        @Query("language") language: String = "en-US",
    ): CastsCrews

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilars(
        @Path("movie_id") id: Int,
        @Query("api_key") key: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int,
    ): BaseResponse<List<MovieSummary>>

    @GET("movie/{movie_id}/reviews")
    suspend fun getReviews(
        @Path("movie_id") id: Int,
        @Query("api_key") key: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int,
    ): BaseResponse<List<Comment>>

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") id: Int,
        @Query("api_key") key: String,
        @Query("language") language: String = "en-US",
    ): BaseResponse<List<Video>>

    @GET("discover/movie")
    suspend fun getGenreMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
        @Query("with_genres") genreId: Int,
    ): BaseResponse<List<MovieSummary>>
}
