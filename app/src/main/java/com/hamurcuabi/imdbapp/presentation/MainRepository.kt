package com.hamurcuabi.imdbapp.presentation

import com.hamurcuabi.imdbapp.core.base.BaseRepository
import com.hamurcuabi.imdbapp.core.utils.NetworkHelper
import com.hamurcuabi.imdbapp.data.network.api.ApiHelper
import com.hamurcuabi.imdbapp.di.DispatcherProvider
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiHelper: ApiHelper,
    networkHelper: NetworkHelper,
    dispatcher: DispatcherProvider
) : BaseRepository(networkHelper, dispatcher) {

    fun getNowPlayingMovieList() = baseRequestFlow { apiHelper.getNowPlayingMovieList() }

    fun getUpcomingMovieList(page: Int) =
        baseRequestFlow { apiHelper.getUpcomingMovieList(page) }

    fun getMovieDetail(id: Int) = baseRequestFlow { apiHelper.getMovieDetail(id) }
}