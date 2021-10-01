package com.example.demohilts.data.service

data class BaseResults<out T>(val status: CoroutineState, val data: T?, val message: String?) {

    companion object {
        fun <T> success(data: T) =
            BaseResults(status = CoroutineState.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String) =
            BaseResults(status = CoroutineState.ERROR, data = data, message = message)

        fun <T> loading(data: T?) =
            BaseResults(status = CoroutineState.LOADING, data = data, message = null)
    }
}

enum class CoroutineState { SUCCESS, ERROR, LOADING }
