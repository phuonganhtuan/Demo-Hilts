package com.example.demohilts.data.repo

import com.example.demohilts.data.service.ApiService
import javax.inject.Inject

class MainRepoImpl @Inject constructor(private val apiService: ApiService) : MainRepo {

    override suspend fun getPopulars(key: String, page: Int) =
        apiService.getDiscover(apiKey = key, page = page)

    override suspend fun getTrendings(key: String) = apiService.getTrendingDay(key)

    override suspend fun getGenres(key: String) = apiService.getGenres(apiKey = key)

    override suspend fun getMovieDetail(id: Int, key: String) =
        apiService.getMovieDetail(id = id, key = key)

    override suspend fun getCastsAndCrews(id: Int, key: String) =
        apiService.getCastsAndCrews(id = id, key = key)

    override suspend fun getSimilars(
        id: Int,
        key: String,
        page: Int
    ) = apiService.getSimilars(id = id, key = key, page = page)

    override suspend fun getReviews(id: Int, key: String, page: Int) =
        apiService.getReviews(id = id, key = key, page = page)

    override suspend fun getVideos(id: Int, key: String) = apiService.getMovieVideos(id, key)

    override suspend fun getGenreMovies(
        key: String,
        page: Int,
        genreId: Int
    ) = apiService.getGenreMovies(key, page, genreId)
}
