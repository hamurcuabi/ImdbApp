package com.hamurcuabi.imdbapp.presentation.detail

import androidx.fragment.app.viewModels
import com.hamurcuabi.imdbapp.core.base.BaseFragment
import com.hamurcuabi.imdbapp.databinding.FragmentDetailBinding

class DetailFragment :
    BaseFragment<FragmentDetailBinding, DetailViewModel.DetailViewState, DetailViewModel.DetailViewEffect, DetailViewModel.DetailViewEvent, DetailViewModel>(
        FragmentDetailBinding::inflate
    ) {
    override val viewModel: DetailViewModel by viewModels()

    override fun init() {
    }

    override fun renderViewState(viewState: DetailViewModel.DetailViewState) {
    }

    override fun renderViewEffect(viewEffect: DetailViewModel.DetailViewEffect) {
    }
}