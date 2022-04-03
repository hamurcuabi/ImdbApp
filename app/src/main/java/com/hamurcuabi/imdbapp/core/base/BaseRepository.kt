package com.hamurcuabi.imdbapp.core.base

import com.hamurcuabi.imdbapp.R
import com.hamurcuabi.imdbapp.core.utils.NetworkHelper
import com.hamurcuabi.imdbapp.core.utils.NetworkResource
import com.hamurcuabi.imdbapp.core.utils.Resource
import com.hamurcuabi.imdbapp.core.utils.ResourceProvider
import com.hamurcuabi.imdbapp.di.DispatcherProvider
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

abstract class BaseRepository constructor(
    private val networkHelper: NetworkHelper,
    private val resourceProvider: ResourceProvider,
    private val dispatcher: DispatcherProvider
) {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): NetworkResource<T> {
        return withContext(dispatcher.io) {
            if (networkHelper.isNetworkConnected()) {
                try {
                    val result = apiCall.invoke()
                    when ((result as Response<*>).code()) {
                        in 200..300 -> NetworkResource.Success(result)
                        401 -> NetworkResource.Error(resourceProvider.getString(R.string.auth_error))
                        else -> NetworkResource.Error(result.message())
                    }
                } catch (throwable: Throwable) {
                    when (throwable) {
                        is HttpException -> NetworkResource.Error(throwable.message())
                        is SocketTimeoutException -> NetworkResource.Error(
                            resourceProvider.getString(
                                R.string.socket_exception
                            )
                        )
                        is IOException -> NetworkResource.Error(resourceProvider.getString(R.string.io_exception))
                        else -> NetworkResource.Error(resourceProvider.getString(R.string.unexpected_error))
                    }
                }

            } else {
                NetworkResource.Error(resourceProvider.getString(R.string.no_internet_connection))
            }
        }
    }

    fun <T> baseFlowCreator(
        apiCall: suspend () -> Response<T>
    ) = flow {
        emit(Resource.Loading)
        val networkResponse = safeApiCall {
            apiCall.invoke()
        }
        val response = when (networkResponse) {
            is NetworkResource.Success -> Resource.Success(networkResponse.data?.body())
            is NetworkResource.Error -> Resource.Failure(networkResponse.message)
        }
        emit(response)
    }
}