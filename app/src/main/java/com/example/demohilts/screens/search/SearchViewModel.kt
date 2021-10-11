package com.example.demohilts.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demohilts.base.BaseResponse
import com.example.demohilts.data.entity.KeyWord
import com.example.demohilts.data.entity.MovieSummary
import com.example.demohilts.data.repo.MainRepo
import com.example.demohilts.data.service.BaseResults
import com.example.demohilts.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repo: MainRepo) : ViewModel() {

    var results: MutableStateFlow<BaseResults<BaseResponse<List<MovieSummary>>>> =
        MutableStateFlow(BaseResults.loading(null))

    val keyWords: MutableStateFlow<BaseResults<BaseResponse<List<KeyWord>>>> =
        MutableStateFlow(BaseResults.loading(null))

    fun searchMovies(page: Int, query: String) = viewModelScope.launch {
        results.value = BaseResults.loading(null)
        try {
            val resultsRes = repo.searchMovies(Constants.apiKey, page, query)
            results.value = BaseResults.success(resultsRes)
        } catch (exception: Exception) {
            results.value = BaseResults.error(null, "Cannot search with this key word.")
        }
    }

    fun searchKWs(page: Int = 1, query: String) = viewModelScope.launch {
        keyWords.value = BaseResults.loading(null)
        try {
            val keyWordsRes = repo.searchKW(Constants.apiKey, page, query)
            keyWords.value = BaseResults.success(keyWordsRes)
        } catch (exception: Exception) {
            keyWords.value = BaseResults.error(null, "Cannot search with this key word.")
        }
    }
}
