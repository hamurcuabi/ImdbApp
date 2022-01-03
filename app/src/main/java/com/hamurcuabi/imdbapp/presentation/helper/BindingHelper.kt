package com.hamurcuabi.imdbapp.presentation.helper

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.hamurcuabi.imdbapp.core.loadWithGlide
import com.hamurcuabi.imdbapp.data.network.model.common.MovieOverview
import com.hamurcuabi.imdbapp.presentation.home.MovieOverviewSliderAdapter
import com.hamurcuabi.imdbapp.presentation.home.UpcomingMovieRecyclerViewAdapter

@BindingAdapter("loadImage")
fun loadImage(view: ImageView, url: String) {
    val prefix = "https://www.themoviedb.org/t/p/w1280"
    view.loadWithGlide(prefix + url)
}

@BindingAdapter("submitList")
fun setMovieOverviewRecyclerViewAdapter(recyclerView: RecyclerView, items: List<MovieOverview>?) {
    items?.let {
        if (recyclerView.adapter is UpcomingMovieRecyclerViewAdapter) {
            (recyclerView.adapter as UpcomingMovieRecyclerViewAdapter).submitList(items)
        }
    }
}

@BindingAdapter("submitListViewPager2")
fun setViewPager2Adapter(view: ViewPager2, items: List<MovieOverview>?) {
    items?.let {
        if (view.adapter is MovieOverviewSliderAdapter) {
            (view.adapter as MovieOverviewSliderAdapter).submitList(items)
        }
    }
}