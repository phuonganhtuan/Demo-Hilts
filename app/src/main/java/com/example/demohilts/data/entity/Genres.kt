package com.example.demohilts.data.entity

data class Genres(
    val genres: List<Genre>?
)

data class Genre(
    val id: Int?,
    val name: String?,
)
