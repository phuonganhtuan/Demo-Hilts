package com.example.demohilts.data.service

import com.example.demohilts.data.entity.Coin
import dagger.Provides
import retrofit2.http.GET

interface ApiService {

    @GET("bpi/currentprice.json")
    suspend fun getBTCPrice(): Coin
}
