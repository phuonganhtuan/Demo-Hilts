package com.example.demohilts.data.entity

data class MovieDetail(
    val adult: Boolean,
    val backdrop_path: String?,
    val budget: Int?,
    val genres: List<Genre>?,
    val homepage: String?,
    val id: Int?,
    val imdb_id: String?,
    val original_language: String?,
    val original_title: String?,
    val overview: String?,
    val popularity: Double?,
    val poster_path: String?,
    val production_companies: List<Company>?,
    val production_countries: List<ProductionCountry>?,
    val release_date: String?,
    val revenue: Int?,
    val runtime: Int?,
    val spoken_languages: List<SpokenLanguage>?,
    val status: String?,
    val tagline: String?,
    val title: String?,
    val video: Boolean?,
    val vote_average: Double?,
    val vote_count: Int?,
    val name: String?,
    val media_type: String?,
) {
    fun getCompaniesText(): String {
        var result = ""
        if (!production_companies.isNullOrEmpty()) {
            production_companies.forEach {
                result += "\n- ${it.name}"
            }
        }
        return result
    }
}
