package com.hamurcuabi.imdbapp.presentation

import com.hamurcuabi.imdbapp.core.base.BaseRepository
import com.hamurcuabi.imdbapp.core.utils.NetworkHelper
import com.hamurcuabi.imdbapp.core.utils.ResourceProvider
import com.hamurcuabi.imdbapp.data.network.api.ApiHelper
import com.hamurcuabi.imdbapp.di.DispatcherProvider
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiHelper: ApiHelper,
    networkHelper: NetworkHelper,
    resourceProvider: ResourceProvider,
    dispatcher: DispatcherProvider
) : BaseRepository(networkHelper, resourceProvider, dispatcher) {

    fun getNowPlayingMovieList() = baseFlowCreator { apiHelper.getNowPlayingMovieList() }

    fun getUpcomingMovieList(page: Int) =
        baseFlowCreator { apiHelper.getUpcomingMovieList(page) }

    fun getMovieDetail(id: Int) = baseFlowCreator { apiHelper.getMovieDetail(id) }
}