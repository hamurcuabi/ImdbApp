package com.hamurcuabi.imdbapp.data.network.api

import com.hamurcuabi.imdbapp.data.network.model.responses.MovieListResponse
import retrofit2.Response

interface ApiHelper {

    suspend fun getNowPlayingMovieList():Response<MovieListResponse>
}