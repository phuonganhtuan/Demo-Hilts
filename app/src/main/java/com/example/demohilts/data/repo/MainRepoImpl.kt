package com.example.demohilts.data.repo

import com.example.demohilts.data.service.ApiService
import javax.inject.Inject

class MainRepoImpl @Inject constructor(private val apiService: ApiService) : MainRepo {

    override suspend fun getPopulars(key: String, page: Int) =
        apiService.getDiscover(apiKey = key, page = page)

    override suspend fun getTrendings(key: String) = apiService.getTrendingDay(apiKey = key)

    override suspend fun getGenres(key: String) = apiService.getGenres(apiKey = key)

    override suspend fun getMovieDetail(id: Int, key: String, type: String) =
        apiService.getMovieDetail(id = id, key = key, type = type)

    override suspend fun getCastsAndCrews(id: Int, key: String, type: String) =
        apiService.getCastsAndCrews(id = id, key = key, type = type)

    override suspend fun getSimilars(
        id: Int,
        key: String,
        page: Int
        , type: String
    ) = apiService.getSimilars(id = id, key = key, page = page, type = type)

    override suspend fun getReviews(id: Int, key: String, page: Int, type: String) =
        apiService.getReviews(id = id, key = key, page = page, type = type)

    override suspend fun getVideos(id: Int, key: String, type: String) = apiService.getMovieVideos(id = id, key = key, type = type)

    override suspend fun getGenreMovies(
        key: String,
        page: Int,
        genreId: Int
    ) = apiService.getGenreMovies(key, page, genreId)

    override suspend fun searchMovies(
        key: String,
        page: Int,
        query: String
    ) = apiService.searchMovies(apiKey = key, page = page, query = query)

    override suspend fun searchKW(key: String, page: Int, query: String) =
        apiService.searchKW(key, query, page)

    override suspend fun getMovieImages(id: Int, key: String, type: String) = apiService.getMovieImages(id = id, key = key, type = type)

    override suspend fun getMovieKeyWords(id: Int, key: String, type: String) = apiService.getMovieKWs(id = id, key = key, type = type)
}
