package com.hamurcuabi.imdbapp.core.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import com.hamurcuabi.imdbapp.core.InflateFragmentView

abstract class BaseFragment<VB : ViewBinding, STATE, EFFECT, EVENT, ViewModel : BaseMVIViewModel<STATE, EFFECT, EVENT>>(
    private val inflateFragmentView: InflateFragmentView<VB>
) : Fragment() {
    private val TAG = "BaseFragment"
    abstract val viewModel: ViewModel
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    abstract fun init()

    abstract fun renderViewState(viewState: STATE)

    abstract fun renderViewEffect(viewEffect: EFFECT)

    private val viewStateObserver = Observer<STATE> {
        Log.d(TAG, "observed viewState : $it")
        renderViewState(it)
    }

    private val viewEffectObserver = Observer<EFFECT> {
        Log.d(TAG, "observed viewEffect : $it")
        renderViewEffect(it)
    }

    /**
     * Navigate to an action
     */
    protected fun navigateTo(actionId: Int) {
        Navigation.findNavController(binding.root).navigate(actionId)
    }

    protected fun navigateTo(directions: NavDirections) {
        Navigation.findNavController(binding.root).navigate(directions)
    }

    protected fun navigateBack() {
        Navigation.findNavController(binding.root).popBackStack()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateFragmentView.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewStates().observe(this, viewStateObserver)
        viewModel.viewEffects().observe(this, viewEffectObserver)
        init()
    }
}
