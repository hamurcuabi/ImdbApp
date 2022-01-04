package com.hamurcuabi.imdbapp.presentation.helper

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.facebook.shimmer.ShimmerFrameLayout
import com.hamurcuabi.imdbapp.R
import com.hamurcuabi.imdbapp.core.exhaustive
import com.hamurcuabi.imdbapp.core.loadWithGlide
import com.hamurcuabi.imdbapp.data.network.model.common.MovieOverview
import com.hamurcuabi.imdbapp.presentation.home.HomeViewModel
import com.hamurcuabi.imdbapp.presentation.home.MovieOverviewSliderAdapter
import com.hamurcuabi.imdbapp.presentation.home.UpcomingMovieRecyclerViewAdapter

@BindingAdapter("loadImage")
fun loadImage(view: ImageView, url: String?) {
    url?.let {
        val prefix = "https://www.themoviedb.org/t/p/w1280"
        view.loadWithGlide(prefix + it)
    } ?: run {
        view.loadWithGlide(R.drawable.ic_no_image)
    }
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

@BindingAdapter("customVisibility")
fun setVisibility(view: View, exp: Boolean?) {
    val visibility = when (exp) {
        true -> View.VISIBLE
        false -> View.GONE
        else -> View.VISIBLE
    }.exhaustive

    view.visibility = visibility
}

@BindingAdapter("textAverage")
fun setAverageText(view: TextView, average: Double) {
    val text = "$average/10"
    view.text = text
}