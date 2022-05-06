package com.hamurcuabi.imdbapp.presentation.home

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.hamurcuabi.imdbapp.core.base.BaseFragment
import com.hamurcuabi.imdbapp.core.utils.exhaustive
import com.hamurcuabi.imdbapp.databinding.FragmentHomeBinding
import com.hamurcuabi.imdbapp.presentation.helper.onLoadMoreScrollListener
import com.hamurcuabi.imdbapp.presentation.home.HomeViewModel.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding,
        HomeViewState,
        HomeViewEffect,
        HomeViewEvent,
        HomeViewModel>(FragmentHomeBinding::inflate) {

    companion object {
        const val DELAY_TIME = 100L
    }

    private lateinit var nowPlayingMovieAdapter: NowPlayingMovieAdapter
    private lateinit var upcomingMovieAdapter: UpcomingMovieAdapter

    override val viewModel: HomeViewModel by viewModels()

    override fun init() {
        setupSwipeRefresh()
        setupAdapters()
        initScrollListener()
        setupIndicator()
        setObservers()
        viewModel.process(HomeViewEvent.IdleState)
    }


    private fun bindViewState(viewState: HomeViewState) {
        binding.apply {
            this.viewState = viewState
            swipeRefresh.isRefreshing = viewState.isLoadingNowPlayingList
        }
    }

    override fun renderViewState(viewState: HomeViewState) {
        bindViewState(viewState)
    }

    override fun renderViewEffect(viewEffect: HomeViewEffect) {
        when (viewEffect) {
            is HomeViewEffect.GoToDetailPage -> navigateToDetail(viewEffect.movieId)
        }.exhaustive
    }

    private fun setObservers() {
        lifecycleScope.launchWhenResumed {
            viewModel.currentPage.collectLatest {
                // TODO Viewpager2 known issue. So added delay to fix it.
                delay(DELAY_TIME)
                binding.vp2MovieOverview.setCurrentItem(it, true)
            }
        }
    }

    private fun refreshData() {
        viewModel.process(HomeViewEvent.RefreshAll)
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.apply {
            setOnRefreshListener {
                refreshData()
            }
        }
    }

    private fun setupIndicator() {
        TabLayoutMediator(binding.intoTabLayout, binding.vp2MovieOverview)
        { _, _ -> }.attach()
    }

    private fun setupAdapters() {
        nowPlayingMovieAdapter = NowPlayingMovieAdapter {
            viewModel.process(HomeViewEvent.ClickToItem(it))
        }
        upcomingMovieAdapter = UpcomingMovieAdapter {
            viewModel.process(HomeViewEvent.ClickToItem(it))
        }
        binding.apply {
            vp2MovieOverview.adapter = nowPlayingMovieAdapter
            rcvUpcoming.adapter = upcomingMovieAdapter
        }
    }

    private fun navigateToDetail(movieId: Int) {
        val directions = HomeFragmentDirections.actionHomeFragmentToDetailFragment(movieId)
        navigateTo(directions)
    }

    private fun initScrollListener() {
        binding.rcvUpcoming.onLoadMoreScrollListener {
            viewModel.process(HomeViewEvent.LoadMore)
        }
    }

}