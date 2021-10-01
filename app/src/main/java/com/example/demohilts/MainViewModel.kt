package com.example.demohilts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demohilts.data.entity.Coin
import com.example.demohilts.data.repo.MainRepo
import com.example.demohilts.data.service.BaseResults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repo: MainRepo) : ViewModel() {

    var btcPrice: MutableStateFlow<BaseResults<Coin>> = MutableStateFlow(BaseResults.loading(null))

    fun getBTCPrice() = viewModelScope.launch {
        btcPrice.value = BaseResults.loading(null)
        try {
            val price = repo.getBTCPrice()
            btcPrice.value = BaseResults.success(price)
        } catch (exception: Exception) {
            btcPrice.value = BaseResults.error(null, "Cannot get newest price.")
        }
    }
}
