package com.hamurcuabi.imdbapp.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import com.hamurcuabi.imdbapp.core.utils.InflateFragmentView


abstract class BaseFragment<VB : ViewBinding, STATE, EFFECT, EVENT, ViewModel : BaseMVIViewModel<STATE, EFFECT, EVENT>>(
    private val inflateFragmentView: InflateFragmentView<VB>
) : Fragment() {
    private var loadingDialog: AlertDialog? = null

    abstract val viewModel: ViewModel
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    abstract fun init()

    abstract fun renderViewState(viewState: STATE)

    abstract fun renderViewEffect(viewEffect: EFFECT)

    private val viewStateObserver = Observer<STATE> {
        renderViewState(it)
    }

    private val viewEffectObserver = Observer<EFFECT> {
        renderViewEffect(it)
    }

    protected fun navigateTo(actionId: Int) {
        Navigation.findNavController(binding.root).navigate(actionId)
    }

    protected fun navigateTo(directions: NavDirections) {
        Navigation.findNavController(binding.root).navigate(directions)
    }

    protected fun navigateBack() {
        Navigation.findNavController(binding.root).popBackStack()
    }

    protected fun showToast(text: String) {
        context?.let {
            Toast.makeText(it, text, Toast.LENGTH_SHORT).show()
        }
    }

    protected open fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    protected open fun showLoadingDialog() {
        context?.let { context ->
            if (loadingDialog == null) {
                val progressBar = ProgressBar(context)
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                progressBar.layoutParams = lp

                loadingDialog = AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setView(progressBar)
                    .create()
            }
            loadingDialog?.show()
        }
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
        viewModel.viewStates().observe(viewLifecycleOwner, viewStateObserver)
        viewModel.viewEffects().observe(viewLifecycleOwner, viewEffectObserver)
        init()
        observeShowLoading()
    }

    private fun observeShowLoading() {
        viewModel.showLoading.observe(viewLifecycleOwner) { shouldShow ->
            if (shouldShow) {
                showLoadingDialog()
            } else {
                hideLoadingDialog()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
