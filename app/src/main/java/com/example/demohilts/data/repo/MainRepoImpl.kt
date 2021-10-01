package com.example.demohilts.data.repo

import android.util.Log
import com.example.demohilts.data.entity.Coin
import com.example.demohilts.data.service.ApiService
import javax.inject.Inject

class MainRepoImpl @Inject constructor(private val apiService: ApiService) : MainRepo {

    override suspend fun getBTCPrice(): Coin {
        return apiService.getBTCPrice()
    }
}