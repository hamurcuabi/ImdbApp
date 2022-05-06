package com.hamurcuabi.imdbapp.presentation.detail

import com.hamurcuabi.imdbapp.core.base.BaseMVIViewModel
import com.hamurcuabi.imdbapp.data.network.model.responses.MovieDetailResponse
import com.hamurcuabi.imdbapp.presentation.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val mainRepository: MainRepository,
) : BaseMVIViewModel<DetailViewModel.DetailViewState, DetailViewModel.DetailViewEffect, DetailViewModel.DetailViewEvent>(
) {

    init {
        viewState = DetailViewState()
    }

    override fun process(viewEvent: DetailViewEvent) {
        super.process(viewEvent)
        when (viewEvent) {
            is DetailViewEvent.GetMovieDetail -> fetchMovieDetail(viewEvent.id)
            is DetailViewEvent.ShowError -> navigateBackWithErrorMessage(viewEvent.message)
        }
    }

    private fun navigateBackWithErrorMessage(message: String) {
        viewEffect = DetailViewEffect.NavigateBack
    }

    private fun fetchMovieDetail(id: Int) {
        makeApiCall(
            onFailure = {

            },
            onLoading = {
                viewState = viewState.copy(isLoading = true)
            },
            onSuccess = {
                viewState = viewState.copy(
                    movieDetail = it,
                    isLoading = false
                )
            }
        ) { mainRepository.getMovieDetail(id) }

    }

    sealed class DetailViewEvent {
        data class GetMovieDetail(val id: Int) : DetailViewEvent()
        data class ShowError(val message: String) : DetailViewEvent()
    }

    data class DetailViewState(
        val movieDetail: MovieDetailResponse? = null,
        val isLoading: Boolean = false
    )

    sealed class DetailViewEffect {
        object NavigateBack : DetailViewEffect()
    }
}