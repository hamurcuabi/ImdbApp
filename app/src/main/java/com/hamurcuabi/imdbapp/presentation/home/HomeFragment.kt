package com.hamurcuabi.imdbapp.presentation.home

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.hamurcuabi.imdbapp.core.base.BaseFragment
import com.hamurcuabi.imdbapp.core.utils.exhaustive
import com.hamurcuabi.imdbapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel.HomeViewState, HomeViewModel.HomeViewEffect, HomeViewModel.HomeViewEvent, HomeViewModel>(
        FragmentHomeBinding::inflate
    ) {

    private lateinit var nowPlayingMovieAdapter: NowPlayingMovieAdapter
    private lateinit var upcomingMovieAdapter: UpcomingMovieAdapter

    override val viewModel: HomeViewModel by viewModels()

    override fun init() {
        setupSwipeRefresh()
        setupAdapters()
        initScrollListener()
        setupIndicator()
        setObservers()
        viewModel.process(HomeViewModel.HomeViewEvent.ObserveState)
    }

    private fun setObservers() {
        lifecycleScope.launchWhenResumed {
            viewModel.currentPage.collectLatest {
                // TODO Viewpager2 known issue. So added delay to fix it.
                delay(100)
                binding.vp2MovieOverview.setCurrentItem(it, true)
            }
        }
    }

    private fun refreshData() {
        viewModel.process(HomeViewModel.HomeViewEvent.RefreshAll)
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
            viewModel.process(HomeViewModel.HomeViewEvent.ClickToItem(it))
        }
        upcomingMovieAdapter = UpcomingMovieAdapter {
            viewModel.process(HomeViewModel.HomeViewEvent.ClickToItem(it))
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
        binding.rcvUpcoming.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val lastPosition = linearLayoutManager!!.findLastCompletelyVisibleItemPosition()
                if (lastPosition == upcomingMovieAdapter.itemCount - 1) {
                    viewModel.process(HomeViewModel.HomeViewEvent.LoadMore)
                }
            }
        })
    }

    private fun bindViewState(viewState: HomeViewModel.HomeViewState) {
        binding.apply {
            this.viewState = viewState
            swipeRefresh.isRefreshing = viewState.isLoadingNowPlayingList
        }
    }

    override fun renderViewState(viewState: HomeViewModel.HomeViewState) {
        bindViewState(viewState)
    }

    override fun renderViewEffect(viewEffect: HomeViewModel.HomeViewEffect) {
        when (viewEffect) {
            is HomeViewModel.HomeViewEffect.GoToDetailPage -> navigateToDetail(viewEffect.movieId)
            is HomeViewModel.HomeViewEffect.ShowToast -> showToast(viewEffect.message)
        }.exhaustive
    }
}