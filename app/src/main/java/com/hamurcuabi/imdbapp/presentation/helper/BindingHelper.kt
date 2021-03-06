package com.hamurcuabi.imdbapp.presentation.helper

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.hamurcuabi.imdbapp.R
import com.hamurcuabi.imdbapp.core.utils.exhaustive
import com.hamurcuabi.imdbapp.core.utils.loadWithGlide

const val PREFIX_IMAGE_URL = "https://www.themoviedb.org/t/p/w1280"

@BindingAdapter("loadImage")
fun ImageView.loadImage(url: String?) {
    url?.let {
        this.loadWithGlide(PREFIX_IMAGE_URL + it)
    } ?: run {
        this.loadWithGlide(R.drawable.ic_no_image)
    }
}

@BindingAdapter("submitList")
fun <T> RecyclerView.submitList(items: List<T>?) {
    items?.let {
        val adapter = (this.adapter as androidx.recyclerview.widget.ListAdapter<T, *>)
        adapter.submitList(items)
    }
}

@BindingAdapter("submitListViewPager2Generic")
fun <T> ViewPager2.submitListViewPager2Generic(items: List<T>?) {
    items?.let {
        val adapter = (this.adapter as androidx.recyclerview.widget.ListAdapter<T, *>)
        adapter.submitList(items)
    }
}

@BindingAdapter("bindingVisibility")
fun View.setVisibility(exp: Boolean?) {
    val visibility = when (exp) {
        true -> View.VISIBLE
        false -> View.GONE
        else -> View.VISIBLE
    }.exhaustive

    this.visibility = visibility
}

@BindingAdapter("textAverage")
fun TextView.setAverageText(average: Double) {
    val text = "$average/10"
    this.text = text
}


@BindingAdapter("isRefreshing")
fun SwipeRefreshLayout.setIsRefreshing(isRefreshing: Boolean?) {
    isRefreshing?.let {
        this.isRefreshing = it
    }
}

@BindingAdapter("onLoadMoreScrollListener")
fun RecyclerView.onLoadMoreScrollListener(onLoadMore: () -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val linearLayoutManager = recyclerView.layoutManager as? LinearLayoutManager
            val lastPosition = linearLayoutManager?.findLastCompletelyVisibleItemPosition()
            lastPosition?.let {
                adapter?.let { adapter ->
                    if (it == adapter.itemCount - 1) {
                        onLoadMore.invoke()
                    }
                }
            }
        }
    })
}