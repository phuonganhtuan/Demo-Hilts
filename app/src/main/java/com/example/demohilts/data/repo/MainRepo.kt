package com.example.demohilts.data.repo

import com.example.demohilts.data.entity.Coin

interface MainRepo {

    suspend fun getBTCPrice(): Coin
}
