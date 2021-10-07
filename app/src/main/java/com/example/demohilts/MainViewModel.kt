package com.example.demohilts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demohilts.base.BaseResponse
import com.example.demohilts.data.entity.Genres
import com.example.demohilts.data.entity.MovieDetail
import com.example.demohilts.data.entity.MovieSummary
import com.example.demohilts.data.repo.MainRepo
import com.example.demohilts.data.service.BaseResults
import com.example.demohilts.utils.Constants.apiKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repo: MainRepo) : ViewModel() {

    var popularMovies: MutableStateFlow<BaseResults<BaseResponse<List<MovieSummary>>>> =
        MutableStateFlow(BaseResults.loading(null))

    var trendingMovies: MutableStateFlow<BaseResults<BaseResponse<List<MovieSummary>>>> =
        MutableStateFlow(BaseResults.loading(null))

    var genres: MutableStateFlow<BaseResults<Genres>> = MutableStateFlow(BaseResults.loading(null))

    fun getPopularMovies(page: Int) = viewModelScope.launch {
        popularMovies.value = BaseResults.loading(null)
        try {
            val movies = repo.getPopulars(apiKey, page)
            popularMovies.value = BaseResults.success(movies)
        } catch (exception: Exception) {
            popularMovies.value = BaseResults.error(null, "Cannot get newest popular movies.")
        }
    }

    fun getTrendingMovies() = viewModelScope.launch {
        trendingMovies.value = BaseResults.loading(null)
        try {
            val movies = repo.getTrendings(apiKey)
            trendingMovies.value = BaseResults.success(movies)
        } catch (exception: Exception) {
            trendingMovies.value = BaseResults.error(null, "Cannot get newest trending movies.")
        }
    }

    fun getGenres() = viewModelScope.launch {
        genres.value = BaseResults.loading(null)
        try {
            val genresAsResponse = repo.getGenres(apiKey)
            genres.value = BaseResults.success(genresAsResponse)
        } catch (exception: Exception) {
            genres.value = BaseResults.error(null, "Cannot get newest trending movies.")
        }
    }

    val detail: MutableStateFlow<BaseResults<MovieDetail>> =
        MutableStateFlow(BaseResults.loading(null))

    fun getDetail(id: Int) = viewModelScope.launch {
        detail.value = BaseResults.loading(null)
        try {
            detail.value = BaseResults.success(repo.getMovieDetail(id, apiKey))
        } catch (exception: java.lang.Exception) {
            detail.value = BaseResults.error(null, "Cannot get this movie detail.")
        }
    }
}
