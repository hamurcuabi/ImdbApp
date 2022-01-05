package com.hamurcuabi.imdbapp.core.utils

import android.content.Context

class ResourceProvider constructor(private val context: Context) {
    fun getString(id: Int): String = context.getString(id)
}