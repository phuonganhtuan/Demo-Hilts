package com.example.demohilts.screens.genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demohilts.base.BaseResponse
import com.example.demohilts.data.entity.*
import com.example.demohilts.data.repo.MainRepo
import com.example.demohilts.data.service.BaseResults
import com.example.demohilts.utils.Constants.apiKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class GenreMoviesVM @Inject constructor(private val repo: MainRepo) : ViewModel() {

    var movies: MutableStateFlow<BaseResults<BaseResponse<List<MovieSummary>>>> =
        MutableStateFlow(BaseResults.loading(null))

    fun getGenreMovies(page: Int, genreId: Int) = viewModelScope.launch {
        movies.value = BaseResults.loading(null)
        try {
            val moviesRes = repo.getGenreMovies(apiKey, page, genreId)
            movies.value = BaseResults.success(moviesRes)
        } catch (exception: Exception) {
            movies.value = BaseResults.error(null, "Cannot get this genre movies.")
        }
    }
}
