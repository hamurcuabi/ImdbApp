package com.hamurcuabi.imdbapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hamurcuabi.imdbapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ImdbApp)
        setContentView(R.layout.activity_main)
    }
}