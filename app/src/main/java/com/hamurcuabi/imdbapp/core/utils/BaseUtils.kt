package com.hamurcuabi.imdbapp.core

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hamurcuabi.imdbapp.R


typealias InflateActivityView<T> = (LayoutInflater) -> T

typealias InflateFragmentView<T> = (LayoutInflater, ViewGroup?, Boolean) -> T


internal val Any.TAG: String
    get() {
        return if (!javaClass.isAnonymousClass) {
            val name = javaClass.simpleName
            // first 23 chars
            if (name.length <= 23) name else name.substring(0, 23)
        } else {
            val name = javaClass.name
            // last 23 chars
            if (name.length <= 23) name else name.substring(name.length - 23, name.length)
        }
    }

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
        .fitCenter()
        .error(R.drawable.ic_no_image)
        .into(this)
}

fun ImageView.loadWithGlide(@DrawableRes drawable: Int) {
    Glide.with(this.context)
        .load(drawable)
        .fitCenter()
        .into(this)
}

/**
 * use it with when, to add all remaining branches
 */
val <T> T.exhaustive: T
    get() = this
