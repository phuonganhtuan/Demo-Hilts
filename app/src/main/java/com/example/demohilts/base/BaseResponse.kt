package com.example.demohilts.base

data class BaseResponse<T>(
    val results: T?,
    val page: Int?,
    val total_results: Int?,
)
