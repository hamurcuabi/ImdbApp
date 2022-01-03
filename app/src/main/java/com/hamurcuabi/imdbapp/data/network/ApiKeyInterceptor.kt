package com.hamurcuabi.imdbapp.data.network

import com.hamurcuabi.imdbapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.HttpUrl
import okhttp3.Request

// this interceptor to add api key for every request
class ApiKeyInterceptor :Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val originalHttpUrl: HttpUrl = original.url

        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("api_key", BuildConfig.API_KEY)
            .build()

        val requestBuilder: Request.Builder = original.newBuilder()
            .url(url)
        val request: Request = requestBuilder.build()
        return chain.proceed(request)
    }
}