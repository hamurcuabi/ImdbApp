package com.hamurcuabi.imdbapp.presentation.home

import androidx.fragment.app.viewModels
import com.hamurcuabi.imdbapp.core.base.BaseFragment
import com.hamurcuabi.imdbapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel.HomeViewState, HomeViewModel.HomeViewEffect, HomeViewModel.HomeViewEvent, HomeViewModel>(
        FragmentHomeBinding::inflate
    ) {
    override val viewModel: HomeViewModel by viewModels()

    override fun init() {
        viewModel.process(HomeViewModel.HomeViewEvent.GetNowPlayingMovieList)
    }

    override fun renderViewState(viewState: HomeViewModel.HomeViewState) {
        binding.viewState = viewState
    }

    override fun renderViewEffect(viewEffect: HomeViewModel.HomeViewEffect) {

    }

}