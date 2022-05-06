package com.hamurcuabi.imdbapp.core.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hamurcuabi.imdbapp.R
import com.hamurcuabi.imdbapp.databinding.FragmentLoadingDilaogBinding
import kotlinx.parcelize.Parcelize

class LoadingDialog(private val args: Args) : DialogFragment(R.layout.fragment_loading_dilaog) {

    private lateinit var binding: FragmentLoadingDilaogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoadingDilaogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                return
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvLoadingTitle.text = args.title
        binding.tvLoadingDescription.text = args.description
    }

    override fun dismiss() {
        super.dismissAllowingStateLoss()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.attributes?.windowAnimations = R.style.DialogFragmentAnimation
    }

    companion object {
        const val TAG = "LoadingDialog"
    }

    @Parcelize
    data class Args(
        var title: String? = "",
        var description: String? = "",
    ) : Parcelable
}