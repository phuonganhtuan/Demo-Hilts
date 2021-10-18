package com.example.demohilts.screens.detail

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
class DetailViewModel @Inject constructor(private val repo: MainRepo) : ViewModel() {

    val detail: MutableStateFlow<BaseResults<MovieDetail>> =
        MutableStateFlow(BaseResults.loading(null))

    val castsCrews: MutableStateFlow<BaseResults<CastsCrews>> =
        MutableStateFlow(BaseResults.loading(null))
    val similars: MutableStateFlow<BaseResults<BaseResponse<List<MovieSummary>>>> =
        MutableStateFlow(BaseResults.loading(null))
    val reviews: MutableStateFlow<BaseResults<BaseResponse<List<Comment>>>> =
        MutableStateFlow(BaseResults.loading(null))
    val videos: MutableStateFlow<BaseResults<BaseResponse<List<Video>>>> =
        MutableStateFlow(BaseResults.loading(null))

    val images: MutableStateFlow<BaseResults<Images>> =
        MutableStateFlow(BaseResults.loading(null))

    val kWs: MutableStateFlow<BaseResults<KeyWords>> =
        MutableStateFlow(BaseResults.loading(null))

    fun getDetail(id: Int, type: String) = viewModelScope.launch {
        detail.value = BaseResults.loading(null)
        try {
            val detailMovie = repo.getMovieDetail(id, apiKey, type)
            detail.value = BaseResults.success(detailMovie)
        } catch (exception: Exception) {
            detail.value = BaseResults.error(null, "Cannot get this movie detail.")
        }
    }

    fun getCastsAndCrews(id: Int, type: String) = viewModelScope.launch {
        castsCrews.value = BaseResults.loading(null)
        try {
            val castsCrewsRes = repo.getCastsAndCrews(id, apiKey, type)
            castsCrews.value = BaseResults.success(castsCrewsRes)
        } catch (exception: Exception) {
            castsCrews.value = BaseResults.error(null, "Cannot get this movie casts.")
        }
    }

    fun getSimilarMovies(id: Int, page: Int, type: String) = viewModelScope.launch {
        similars.value = BaseResults.loading(null)
        try {
            val similarsRes = repo.getSimilars(id, apiKey, page, type)
            similars.value = BaseResults.success(similarsRes)
        } catch (exception: Exception) {
            similars.value = BaseResults.error(null, "Cannot get similar movies of this.")
        }
    }

    fun getReviews(id: Int, page: Int, type: String) = viewModelScope.launch {
        reviews.value = BaseResults.loading(null)
        try {
            val reviewsRes = repo.getReviews(id, apiKey, page, type)
            reviews.value = BaseResults.success(reviewsRes)
        } catch (exception: Exception) {
            reviews.value = BaseResults.error(null, "Cannot get this movie reviews.")
        }
    }

    fun getVideos(id: Int, type: String) = viewModelScope.launch {
        videos.value = BaseResults.loading(null)
        try {
            val videosRes = repo.getVideos(id, apiKey, type)
            videos.value = BaseResults.success(videosRes)
        } catch (exception: Exception) {
            videos.value = BaseResults.error(null, "Cannot get this movie videos.")
        }
    }

    fun getImages(id: Int, type: String) = viewModelScope.launch {
        images.value = BaseResults.loading(null)
        try {
            val imagesRes = repo.getMovieImages(id, apiKey, type)
            images.value = BaseResults.success(imagesRes)
        } catch (exception: Exception) {
            images.value = BaseResults.error(null, "Cannot get this movie images.")
        }
    }

    fun getKws(id: Int, type: String) = viewModelScope.launch {
        kWs.value = BaseResults.loading(null)
        try {
            val kWsRes = repo.getMovieKeyWords(id, apiKey, type)
            kWs.value = BaseResults.success(kWsRes)
        } catch (exception: Exception) {
            kWs.value = BaseResults.error(null, "Cannot get this movie key words.")
        }
    }
}
