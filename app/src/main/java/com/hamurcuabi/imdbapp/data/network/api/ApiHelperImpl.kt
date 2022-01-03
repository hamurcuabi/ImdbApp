package com.hamurcuabi.imdbapp.data.network.api

import com.hamurcuabi.imdbapp.data.network.model.responses.MovieListResponse
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {

    override suspend fun getNowPlayingMovieList(): Response<MovieListResponse> =
        apiService.getNowPlayingMovieList()
}