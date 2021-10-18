package com.example.demohilts.utils

import android.content.Context
import com.example.demohilts.data.current.CurrentData

object SPUtils {

    private const val MOVIE_ID_KEY = "movieId"
    private const val MOVIE_TYPE_KEY = "movieType"
    private const val MOVIE_SP_KEY = "movieSP"

    fun saveCurrentId(context: Context, id: Int, type: String) {
        val sp = context.getSharedPreferences(MOVIE_SP_KEY, Context.MODE_PRIVATE)
        sp.edit().putInt(MOVIE_ID_KEY, id).apply()
        sp.edit().putString(MOVIE_TYPE_KEY, type).apply()
        CurrentData.currentType = type
        CurrentData.currentId.value = id
    }

    fun getCurrentId(context: Context): Int {
        val sp = context.getSharedPreferences(MOVIE_SP_KEY, Context.MODE_PRIVATE)
        return sp.getInt(MOVIE_ID_KEY, -1)
    }

    fun getCurrentType(context: Context): String {
        val sp = context.getSharedPreferences(MOVIE_SP_KEY, Context.MODE_PRIVATE)
        return sp.getString(MOVIE_TYPE_KEY, "").toString()
    }
}
