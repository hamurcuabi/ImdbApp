package com.hamurcuabi.imdbapp.presentation.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.hamurcuabi.imdbapp.core.base.BaseMVIViewModel
import com.hamurcuabi.imdbapp.core.utils.Resource
import com.hamurcuabi.imdbapp.data.network.model.common.MovieOverview
import com.hamurcuabi.imdbapp.presentation.MainRepository
import com.hamurcuabi.imdbapp.presentation.home.HomeViewModel.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val mainRepository: MainRepository,
) : BaseMVIViewModel<HomeViewState, HomeViewEffect, HomeViewEvent>(application) {

    init {
        viewState = HomeViewState()
    }

    override fun process(viewEvent: HomeViewEvent) {
        super.process(viewEvent)
        when (viewEvent) {
            is HomeViewEvent.GetNowPlayingMovieList -> fetchNowPlayingMovieList()
            is HomeViewEvent.ClickToItem -> itemClicked(viewEvent.item)
        }
    }

    private fun itemClicked(movieOverViewItem: MovieOverview) {
        viewEffect = HomeViewEffect.ShowToast(movieOverViewItem.toString())
    }

    private fun fetchNowPlayingMovieList() {
        viewModelScope.launch {
            val responseFlow = mainRepository.getNowPlayingMovieList().flowOn(Dispatchers.IO)
            responseFlow.collect {
                when (val response = it) {
                    is Resource.Failure -> {
                        viewState = viewState.copy(isLoading = false)
                        viewEffect = HomeViewEffect.ShowToast(message = response.errorMessage)
                    }
                    is Resource.Loading -> {
                        viewState = viewState.copy(isLoading = true)
                        viewEffect = HomeViewEffect.ShowToast(message = "Loading")
                    }
                    is Resource.Success -> {
                        viewState = viewState.copy(
                            movieOverViewList = response.value?.movieOverviews,
                            isLoading = false
                        )
                        viewEffect = HomeViewEffect.ShowToast(message = "Success")
                    }
                }
            }
        }
    }

    sealed class HomeViewEvent {
        object GetNowPlayingMovieList : HomeViewEvent()
        data class ClickToItem(val item: MovieOverview) : HomeViewEvent()
    }

    data class HomeViewState(
        val movieOverViewList: List<MovieOverview>? = emptyList(),
        val isLoading: Boolean = false
    )

    sealed class HomeViewEffect {
        data class ShowToast(val message: String) : HomeViewEffect()
    }
}