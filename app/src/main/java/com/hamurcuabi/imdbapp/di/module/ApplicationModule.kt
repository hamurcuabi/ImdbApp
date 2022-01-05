package com.hamurcuabi.imdbapp.di.module

import android.content.Context
import com.hamurcuabi.imdbapp.BuildConfig
import com.hamurcuabi.imdbapp.core.utils.NetworkHelper
import com.hamurcuabi.imdbapp.core.utils.ResourceProvider
import com.hamurcuabi.imdbapp.data.network.ApiKeyAndLanguageInterceptor
import com.hamurcuabi.imdbapp.data.network.api.ApiHelper
import com.hamurcuabi.imdbapp.data.network.api.ApiHelperImpl
import com.hamurcuabi.imdbapp.data.network.api.ApiService
import com.hamurcuabi.imdbapp.di.DispatcherImpl
import com.hamurcuabi.imdbapp.di.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Named("BASE_URL")
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(ApiKeyAndLanguageInterceptor())
            .build()
    } else OkHttpClient
        .Builder()
        .build()


    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient, @Named("BASE_URL") baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideApiHelper(apiHelper: ApiHelperImpl): ApiHelper = apiHelper

    @Provides
    @Singleton
    fun provideDispatchers(dispatcherImpl: DispatcherImpl): DispatcherProvider = dispatcherImpl

    @Provides
    @Singleton
    fun provideResourceProvider(@ApplicationContext context: Context) = ResourceProvider(context)

    @Provides
    @Singleton
    fun provideNetworkHelper(@ApplicationContext context: Context) = NetworkHelper(context)
}
