package com.hamurcuabi.imdbapp.core.base

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.hamurcuabi.imdbapp.core.InflateActivityView
import com.hamurcuabi.imdbapp.core.TAG

abstract class BaseActivity<VB : ViewBinding, STATE, EFFECT, EVENT, ViewModel : BaseMVIViewModel<STATE, EFFECT, EVENT>>(
    private val inflateActivityView: InflateActivityView<VB>
) : AppCompatActivity() {
    abstract val viewModel: ViewModel
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    abstract fun init()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflateActivityView.invoke(layoutInflater)
        setContentView(binding.root)
        viewModel.viewStates().observe(this, viewStateObserver)
        viewModel.viewEffects().observe(this, viewEffectObserver)
        init()
    }

    private val viewStateObserver = Observer<STATE> {
        Log.d(TAG, "observed viewState : $it")
        renderViewState(it)
    }

    private val viewEffectObserver = Observer<EFFECT> {
        Log.d(TAG, "observed viewEffect : $it")
        renderViewEffect(it)
    }

    abstract fun renderViewState(viewState: STATE)

    abstract fun renderViewEffect(viewEffect: EFFECT)
}
