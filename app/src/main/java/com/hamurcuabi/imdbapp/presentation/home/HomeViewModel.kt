package com.hamurcuabi.imdbapp.presentation.home

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hamurcuabi.imdbapp.core.base.BaseMVIViewModel
import com.hamurcuabi.imdbapp.core.exhaustive
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
    companion object {
        const val SLIDER_COUNT = 6
        const val INTERVAL = 3000L
        const val MILLIS_IN_FUTURE = INTERVAL * SLIDER_COUNT
    }

    init {
        viewState = HomeViewState()
    }

    private val timer = object : CountDownTimer(MILLIS_IN_FUTURE, INTERVAL) {
        override fun onTick(millisUntilFinished: Long) {
            var page = viewState.currentSliderPage + 1
            if (page == SLIDER_COUNT) {
                page = 0
            }
            viewState = viewState.copy(currentSliderPage = page)
        }

        override fun onFinish() {
            start()
        }
    }

    override fun process(viewEvent: HomeViewEvent) {
        super.process(viewEvent)
        when (viewEvent) {
            is HomeViewEvent.GetNowPlayingMovieList -> fetchNowPlayingMovieList()
            is HomeViewEvent.ClickToItem -> itemClicked(viewEvent.item)
            is HomeViewEvent.GetUpcomingMovieList -> fetchUpcomingMovieList()
        }.exhaustive
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
                            nowPlayingList = response.value?.movieOverviews?.take(SLIDER_COUNT),
                            isLoading = false
                        )
                        timer.start()
                        viewEffect = HomeViewEffect.ShowToast(message = "Success")
                    }
                }
            }
        }
    }

    private fun fetchUpcomingMovieList() {
        viewModelScope.launch {
            val responseFlow = mainRepository.getUpcomingMovieList().flowOn(Dispatchers.IO)
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
                            upcomingList = response.value?.movieOverviews,
                            isLoading = false
                        )
                        resetTimer()
                        viewEffect = HomeViewEffect.ShowToast(message = "Success")
                    }
                }
            }
        }
    }

    private fun resetTimer() {
        viewState = viewState.copy(currentSliderPage = 0)
        timer.cancel()
        timer.start()
    }

    sealed class HomeViewEvent {
        object GetNowPlayingMovieList : HomeViewEvent()
        object GetUpcomingMovieList : HomeViewEvent()
        data class ClickToItem(val item: MovieOverview) : HomeViewEvent()
    }

    data class HomeViewState(
        val upcomingList: List<MovieOverview>? = emptyList(),
        val nowPlayingList: List<MovieOverview>? = emptyList(),
        val isLoading: Boolean = false,
        val currentSliderPage: Int = 0
    )

    sealed class HomeViewEffect {
        data class ShowToast(val message: String) : HomeViewEffect()
    }
}