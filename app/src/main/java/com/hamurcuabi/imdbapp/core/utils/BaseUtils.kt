package com.hamurcuabi.imdbapp.core.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.hamurcuabi.imdbapp.R

typealias InflateFragmentView<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

/**
 * Internal Contract to be implemented by ViewModel
 * Required to intercept and log ViewEvents
 */
internal interface ViewModelContract<EVENT> {
    fun process(viewEvent: EVENT)
}

/**
 * This is a custom NoObserverAttachedException and it does what it's name suggests.
 * Constructs a new exception with the specified detail message.
 * This is thrown, if you have not attached any observer to the LiveData.
 */
class NoObserverAttachedException(message: String) : Exception(message)

/**
 * Basic ext func to load image with glide
 */
fun ImageView.loadWithGlide(url: String?) {
    Glide.with(this.context)
        .load(url)
        .centerInside()
        .error(R.drawable.ic_no_image)
        .into(this)
}

fun ImageView.loadWithGlide(@DrawableRes drawable: Int) {
    Glide.with(this.context)
        .load(drawable)
        .centerInside()
        .into(this)
}

/**
 * use it with when, to add all remaining branches
 */
val <T> T.exhaustive: T
    get() = this
