package com.hamurcuabi.imdbapp.data.network.model.responses

import com.google.gson.annotations.SerializedName
import com.hamurcuabi.imdbapp.data.network.model.common.Dates
import com.hamurcuabi.imdbapp.data.network.model.common.MovieOverview

data class MovieListResponse(
    @SerializedName("dates")
    val dates: Dates,
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val movieOverviews: List<MovieOverview>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)