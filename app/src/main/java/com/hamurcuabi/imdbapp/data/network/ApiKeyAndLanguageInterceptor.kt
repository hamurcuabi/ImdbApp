package com.hamurcuabi.imdbapp.data.network

import com.hamurcuabi.imdbapp.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.*

class ApiKeyAndLanguageInterceptor : Interceptor {
    companion object {
        const val API_KEY_PARAMETER = "api_key"
        const val LANGUAGE_PARAMETER = "language"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val originalHttpUrl: HttpUrl = original.url

        val url = originalHttpUrl.newBuilder()
            .addQueryParameter(API_KEY_PARAMETER, BuildConfig.API_KEY)
            .addQueryParameter(LANGUAGE_PARAMETER, Locale.getDefault().toLanguageTag())
            .build()

        val requestBuilder: Request.Builder = original.newBuilder().url(url)
        val request: Request = requestBuilder.build()
        return chain.proceed(request)
    }
}