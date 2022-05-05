package com.hamurcuabi.imdbapp.presentation.home

import android.os.CountDownTimer
import com.hamurcuabi.imdbapp.core.base.BaseMVIViewModel
import com.hamurcuabi.imdbapp.core.utils.exhaustive
import com.hamurcuabi.imdbapp.data.network.model.common.MovieOverview
import com.hamurcuabi.imdbapp.presentation.MainRepository
import com.hamurcuabi.imdbapp.presentation.home.HomeViewModel.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainRepository: MainRepository,
) : BaseMVIViewModel<HomeViewState, HomeViewEffect, HomeViewEvent>() {

    companion object {
        const val INITIAL_PAGE = 0
        const val SLIDER_COUNT = 6
        const val INTERVAL = 10000L
        const val MILLIS_IN_FUTURE = INTERVAL * SLIDER_COUNT
    }

    init {
        viewState = HomeViewState(
            isLoadingUpcomingList = true,
            isLoadingNowPlayingList = true,
            isPagingLoading = false
        )
        refreshAll()
    }

    private var _currentPage = MutableStateFlow(INITIAL_PAGE)
    val currentPage: StateFlow<Int> = _currentPage

    private val timer = object : CountDownTimer(MILLIS_IN_FUTURE, INTERVAL) {
        override fun onTick(millisUntilFinished: Long) {
            var page = _currentPage.value.plus(1)
            if (page == SLIDER_COUNT) {
                page = INITIAL_PAGE
            }
            _currentPage.value = page
        }

        override fun onFinish() {
            resetTimer()
        }
    }

    override fun process(viewEvent: HomeViewEvent) {
        super.process(viewEvent)
        when (viewEvent) {
            is HomeViewEvent.GetNowPlayingMovieList -> fetchNowPlayingMovieList()
            is HomeViewEvent.ClickToItem -> itemClicked(viewEvent.item)
            is HomeViewEvent.GetUpcomingMovieList -> fetchUpcomingMovieList()
            is HomeViewEvent.LoadMore -> loadMore()
            is HomeViewEvent.StartToSlide -> resetTimer()
            is HomeViewEvent.IdleState -> observeState()
            is HomeViewEvent.RefreshAll -> refreshAll()
        }.exhaustive
    }

    private fun refreshAll() {
        fetchUpcomingMovieList()
        fetchNowPlayingMovieList()
    }

    private fun observeState() {
        viewState = viewState.copy()
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

        makeApiCall(
            onFailure = {
                viewState = viewState.copy(isLoadingNowPlayingList = false)
                viewEffect =
                    HomeViewEffect.ShowToast(message = it?.localizedMessage.toString())
            },
            onSuccess = {
                viewState = viewState.copy(
                    nowPlayingList = it?.movieOverviews?.take(SLIDER_COUNT),
                    isLoadingNowPlayingList = false
                )
                resetTimer()
            },
            onLoading = {
                viewState = viewState.copy(isLoadingNowPlayingList = true)
            },
            showLoading = false
        ) { mainRepository.getNowPlayingMovieList() }
    }

    private fun fetchUpcomingMovieList(page: Int = 1) {
        val isPaging = page > 1
        makeApiCall(
            onFailure = {
                viewState = viewState.copy(
                    isLoadingUpcomingList = false,
                    isPagingLoading = false
                )
                viewEffect =
                    HomeViewEffect.ShowToast(message = it?.localizedMessage.toString())
            },
            onLoading = {
                viewState = viewState.copy(
                    isLoadingUpcomingList = isPaging.not(),
                    isPagingLoading = isPaging
                )
            },
            onSuccess = {
                var upcomingList = it?.movieOverviews
                upcomingList?.let { list ->
                    if (page > 1) {
                        upcomingList = viewState.upcomingList?.plus(list)
                    }
                }
                viewState = viewState.copy(
                    upcomingList = upcomingList,
                    isLoadingUpcomingList = false,
                    isPagingLoading = false,
                    currentApiPage = it?.page ?: 0,
                    maxApiPage = it?.totalPages ?: 100
                )
            },
            showLoading = false
        ) { mainRepository.getUpcomingMovieList(page) }
    }

    private fun resetTimer() {
        timer.cancel()
        timer.start()
    }

    sealed class HomeViewEvent {
        object IdleState : HomeViewEvent()
        object RefreshAll : HomeViewEvent()
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
        val currentApiPage: Int = 1,
        val maxApiPage: Int = 100,
    )

    sealed class HomeViewEffect {
        data class ShowToast(val message: String) : HomeViewEffect()
        data class GoToDetailPage(val movieId: Int) : HomeViewEffect()
    }

}
