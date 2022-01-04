package com.hamurcuabi.imdbapp.presentation.detail

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.hamurcuabi.imdbapp.core.base.BaseMVIViewModel
import com.hamurcuabi.imdbapp.core.utils.Resource
import com.hamurcuabi.imdbapp.data.network.model.common.MovieOverview
import com.hamurcuabi.imdbapp.data.network.model.responses.MovieDetailResponse
import com.hamurcuabi.imdbapp.presentation.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    application: Application,
    private val mainRepository: MainRepository,
) : BaseMVIViewModel<DetailViewModel.DetailViewState, DetailViewModel.DetailViewEffect, DetailViewModel.DetailViewEvent>(
    application
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
        viewEffect = DetailViewEffect.ShowToast(message)
        viewEffect = DetailViewEffect.NavigateBack
    }

    private fun fetchMovieDetail(id: Int) {
        viewModelScope.launch {
            val responseFlow = mainRepository.getMovieDetail(id).flowOn(Dispatchers.IO)
            responseFlow.collect {
                when (val response = it) {
                    is Resource.Failure -> {
                        viewState = viewState.copy(isLoading = false)
                        viewEffect = DetailViewEffect.ShowToast(message = response.errorMessage)
                    }
                    is Resource.Loading -> {
                        viewState = viewState.copy(isLoading = true)
                        viewEffect = DetailViewEffect.ShowToast(message = "Loading")
                    }
                    is Resource.Success -> {
                        viewState = viewState.copy(
                            movieDetail = response.value,
                            isLoading = false
                        )
                        viewEffect = DetailViewEffect.ShowToast(message = "Success")
                    }
                }
            }
        }
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
        data class ShowToast(val message: String) : DetailViewEffect()
        object NavigateBack : DetailViewEffect()
    }
}