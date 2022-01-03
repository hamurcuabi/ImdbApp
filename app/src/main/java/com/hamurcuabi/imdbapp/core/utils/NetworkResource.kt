package com.hamurcuabi.imdbapp.core.utils

sealed class NetworkResource<T>(val data: T?, val message: String) {
    class Success<T>(data: T?) : NetworkResource<T>(data, "Success")
    class Error<T>(message: String) : NetworkResource<T>(null, message)
}