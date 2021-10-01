package com.example.demohilts.di

import com.example.demohilts.data.service.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
object ServiceModules {

    @Provides
    fun provideApiService(
    ): ApiService = Retrofit.Builder()
        .baseUrl("https://api.coindesk.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(ApiService::class.java)
}