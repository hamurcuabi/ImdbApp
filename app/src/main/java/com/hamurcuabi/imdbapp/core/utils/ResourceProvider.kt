package com.hamurcuabi.imdbapp.core.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceProvider @Inject constructor(@ApplicationContext private val context: Context) {
    fun getString(id: Int): String = context.getString(id)
    fun getDrawable(id: Int): Drawable? = ContextCompat.getDrawable(context, id)
}