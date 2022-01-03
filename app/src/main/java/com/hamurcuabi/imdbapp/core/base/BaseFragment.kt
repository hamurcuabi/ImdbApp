package com.hamurcuabi.imdbapp.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.hamurcuabi.imdbapp.core.InflateFragmentView

abstract class BaseFragment<VB : ViewBinding>(
    private val inflateFragmentView: InflateFragmentView<VB>
) : Fragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    abstract fun init()

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
        init()
    }
}