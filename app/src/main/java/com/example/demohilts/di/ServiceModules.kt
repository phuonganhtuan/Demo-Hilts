package com.example.demohilts.di

import com.example.demohilts.data.service.ApiService
import com.example.demohilts.utils.Constants.baseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(ViewModelComponent::class)
object ServiceModules {

    private const val token =
        "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJiMGIyOTI1MDIwNzM1MWVmNjA4NGQwYzA4ZWIwZGQzOSIsInN1YiI6IjYxNWFhMjQ4NjllYjkwMDA4YTNlMzE1OCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.FdfioSTH261-NNADJx_4hB-OCqHwMG4rQa3yAmwArL4"

    @Provides
    fun provideApiService(
    ): ApiService {
//        val httpClient = OkHttpClient()
//        val interceptor = Interceptor {
//            val requestBuilder: Request.Builder = it.request().newBuilder()
//            requestBuilder.header("Content-Type", "application/json")
//            requestBuilder.header("Authorization", "Bearer ${token}")
//            it.proceed(requestBuilder.build())
//        }
//        httpClient.networkInterceptors().add(interceptor)
        return Retrofit.Builder()
            .baseUrl("$baseUrl/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
    }
}