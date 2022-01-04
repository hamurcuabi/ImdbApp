package com.hamurcuabi.imdbapp.presentation.home

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.viewModelScope
import com.hamurcuabi.imdbapp.core.base.BaseMVIViewModel
import com.hamurcuabi.imdbapp.core.utils.exhaustive
import com.hamurcuabi.imdbapp.core.utils.Resource
import com.hamurcuabi.imdbapp.data.network.model.common.MovieOverview
import com.hamurcuabi.imdbapp.presentation.MainRepository
import com.hamurcuabi.imdbapp.presentation.home.HomeViewModel.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
        const val INTERVAL = 5000L
        const val MILLIS_IN_FUTURE = INTERVAL * SLIDER_COUNT
    }

    init {
        viewState = HomeViewState(
            isLoadingUpcomingList = true,
            isLoadingNowPlayingList = true,
            isPagingLoading = false
        )
        fetchNowPlayingMovieList()
        fetchUpcomingMovieList()
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
            is HomeViewEvent.LoadMore -> loadMore()
            is HomeViewEvent.StartToSlide -> startToSlide()
        }.exhaustive
    }

    private fun startToSlide() {
        timer.start()
    }

    private fun loadMore() {
        if (viewState.maxApiPage >= viewState.currentApiPage) {
            val nextPage = viewState.currentApiPage + 1
            viewState = viewState.copy(currentApiPage = nextPage)
            fetchUpcomingMovieList(nextPage)
        }
    }

    private fun itemClicked(movieOverViewItem: MovieOverview) {
        viewEffect = HomeViewEffect.GoToDetailPage(movieOverViewItem.id)
    }

    private fun fetchNowPlayingMovieList() {
        viewModelScope.launch {
            val responseFlow = mainRepository.getNowPlayingMovieList().flowOn(Dispatchers.IO)
            responseFlow.collect {
                when (val response = it) {
                    is Resource.Failure -> {
                        viewState = viewState.copy(isLoadingNowPlayingList = false)
                        viewEffect = HomeViewEffect.ShowToast(message = response.errorMessage)
                    }
                    is Resource.Loading -> {
                        viewState = viewState.copy(isLoadingNowPlayingList = true)
                    }
                    is Resource.Success -> {
                        viewState = viewState.copy(
                            nowPlayingList = response.value?.movieOverviews?.take(SLIDER_COUNT),
                            isLoadingNowPlayingList = false
                        )
                        startToSlide()
                    }
                }
            }
        }
    }

    private fun fetchUpcomingMovieList(page: Int = 1) {
        viewModelScope.launch {
            val responseFlow = mainRepository.getUpcomingMovieList(page).flowOn(Dispatchers.IO)
            val isPaging = page > 1
            responseFlow.collect {
                when (val response = it) {
                    is Resource.Failure -> {
                        viewState =
                            viewState.copy(
                                isLoadingUpcomingList = false,
                                isPagingLoading = false
                            )
                        viewEffect = HomeViewEffect.ShowToast(message = response.errorMessage)
                    }
                    is Resource.Loading -> {
                        viewState =
                            viewState.copy(
                                isLoadingUpcomingList = isPaging.not(),
                                isPagingLoading = isPaging
                            )
                    }
                    is Resource.Success -> {
                        // It means paging
                        var upcomingList = response.value?.movieOverviews
                        upcomingList?.let { list ->
                            if (page > 1) {
                                upcomingList = viewState.upcomingList?.plus(list)
                            }
                        }
                        viewState = viewState.copy(
                            upcomingList = upcomingList,
                            isLoadingUpcomingList = false,
                            isPagingLoading = false,
                            currentApiPage = response.value?.page ?: 0,
                            maxApiPage = response.value?.totalPages ?: 100
                        )
                        resetTimer()
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
        object LoadMore : HomeViewEvent()
        object StartToSlide : HomeViewEvent()
        data class ClickToItem(val item: MovieOverview) : HomeViewEvent()
    }

    data class HomeViewState(
        val upcomingList: List<MovieOverview>? = emptyList(),
        val nowPlayingList: List<MovieOverview>? = emptyList(),
        val isLoadingUpcomingList: Boolean = false,
        val isLoadingNowPlayingList: Boolean = false,
        val isPagingLoading: Boolean = false,
        val currentSliderPage: Int = 0,
        val currentApiPage: Int = 1,
        val maxApiPage: Int = 100,
    )

    sealed class HomeViewEffect {
        data class ShowToast(val message: String) : HomeViewEffect()
        data class GoToDetailPage(val movieId: Int) : HomeViewEffect()
    }
}