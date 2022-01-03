package com.hamurcuabi.imdbapp.data.network.api

import com.hamurcuabi.imdbapp.data.network.model.responses.MovieDetailResponse
import com.hamurcuabi.imdbapp.data.network.model.responses.MovieListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("now_playing")
    suspend fun getNowPlayingMovieList(): Response<MovieListResponse>

    @GET("upcoming")
    suspend fun getUpcomingMovieList(): Response<MovieListResponse>

    @GET("{id}")
    suspend fun getMovieDetail(@Path("id") movieId: Int): Response<MovieDetailResponse>

}