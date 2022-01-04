package com.hamurcuabi.imdbapp.presentation.detail

import androidx.fragment.app.viewModels
import com.hamurcuabi.imdbapp.R
import com.hamurcuabi.imdbapp.core.base.BaseFragment
import com.hamurcuabi.imdbapp.core.utils.exhaustive
import com.hamurcuabi.imdbapp.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment :
    BaseFragment<FragmentDetailBinding, DetailViewModel.DetailViewState, DetailViewModel.DetailViewEffect, DetailViewModel.DetailViewEvent, DetailViewModel>(
        FragmentDetailBinding::inflate
    ) {

    override val viewModel: DetailViewModel by viewModels()

    override fun init() {
        arguments?.let {
            val args = DetailFragmentArgs.fromBundle(it)
            viewModel.process(DetailViewModel.DetailViewEvent.GetMovieDetail(args.movieId))
        } ?: run {
            viewModel.process(DetailViewModel.DetailViewEvent.ShowError(getString(R.string.unexpected_error)))
        }
    }

    override fun renderViewState(viewState: DetailViewModel.DetailViewState) {
        binding.apply {
            this.viewState = viewState
        }
    }

    override fun renderViewEffect(viewEffect: DetailViewModel.DetailViewEffect) {
        when (viewEffect) {
            is DetailViewModel.DetailViewEffect.NavigateBack -> navigateBack()
            is DetailViewModel.DetailViewEffect.ShowToast -> showToast(viewEffect.message)
        }.exhaustive
    }
}