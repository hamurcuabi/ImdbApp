package com.hamurcuabi.imdbapp.presentation

import com.hamurcuabi.imdbapp.core.base.BaseRepository
import com.hamurcuabi.imdbapp.core.utils.NetworkHelper
import com.hamurcuabi.imdbapp.core.utils.NetworkResource
import com.hamurcuabi.imdbapp.core.utils.Resource
import com.hamurcuabi.imdbapp.core.utils.ResourceProvider
import com.hamurcuabi.imdbapp.data.network.api.ApiHelper
import com.hamurcuabi.imdbapp.di.DispatcherProvider
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiHelper: ApiHelper,
    networkHelper: NetworkHelper,
    resourceProvider: ResourceProvider,
    dispatcher: DispatcherProvider
) : BaseRepository(networkHelper, resourceProvider, dispatcher) {

    fun getNowPlayingMovieList() = flow {
        emit(Resource.Loading)
        val networkResponse = safeApiCall {
            apiHelper.getNowPlayingMovieList()
        }
        val response = when (networkResponse) {
            is NetworkResource.Success -> Resource.Success(networkResponse.data?.body())
            is NetworkResource.Error -> Resource.Failure(networkResponse.message)
        }
        emit(response)
    }

    suspend fun getUpcomingMovieList(page:Int) = flow {
        emit(Resource.Loading)
        val networkResponse = safeApiCall {
            apiHelper.getUpcomingMovieList(page)
        }
        val response = when (networkResponse) {
            is NetworkResource.Success -> Resource.Success(networkResponse.data?.body())
            is NetworkResource.Error -> Resource.Failure(networkResponse.message)
        }
        emit(response)
    }

    suspend fun getMovieDetail(id: Int) = flow {
        emit(Resource.Loading)
        val networkResponse = safeApiCall {
            apiHelper.getMovieDetail(id)
        }
        val response = when (networkResponse) {
            is NetworkResource.Success -> Resource.Success(networkResponse.data?.body())
            is NetworkResource.Error -> Resource.Failure(networkResponse.message)
        }
        emit(response)
    }
}