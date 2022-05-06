package com.hamurcuabi.imdbapp.presentation.detail

import androidx.fragment.app.viewModels
import com.hamurcuabi.imdbapp.R
import com.hamurcuabi.imdbapp.core.base.BaseFragment
import com.hamurcuabi.imdbapp.core.utils.exhaustive
import com.hamurcuabi.imdbapp.databinding.FragmentDetailBinding
import com.hamurcuabi.imdbapp.presentation.detail.DetailViewModel.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment :
    BaseFragment<FragmentDetailBinding,
            DetailViewState,
            DetailViewEffect,
            DetailViewEvent,
            DetailViewModel>(FragmentDetailBinding::inflate) {

    override val viewModel: DetailViewModel by viewModels()

    override fun init() {
        arguments?.let {
            val args = DetailFragmentArgs.fromBundle(it)
            viewModel.process(DetailViewEvent.GetMovieDetail(args.movieId))
        } ?: run {
            viewModel.process(DetailViewEvent.ShowError(getString(R.string.unexpected_error)))
        }
    }

    override fun renderViewState(viewState: DetailViewState) {
        binding.apply {
            this.viewState = viewState
        }
    }

    override fun renderViewEffect(viewEffect: DetailViewEffect) {
        when (viewEffect) {
            is DetailViewEffect.NavigateBack -> navigateBack()
        }.exhaustive
    }
}