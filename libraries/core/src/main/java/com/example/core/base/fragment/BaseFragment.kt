package com.example.core.base.fragment

import android.os.Bundle
import android.text.TextUtils
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.example.core.base.BaseViewModel

abstract class BaseFragment<BD : ViewDataBinding, VM : BaseViewModel>(@LayoutRes id: Int) :
    BaseFragmentNotRequireViewModel<BD>(id) {

    private lateinit var viewModel: VM

    abstract fun getVM(): VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getVM()
    }

    override fun initView(savedInstanceState: Bundle?) {

        viewModel.messageError.observe(viewLifecycleOwner) {
            var message = ""
            if (it is String) {
                message = it
            } else {
                if (it is Int) {
                    try {
                        message = getString(it)
                    } catch (e: Exception) {
                        //do nothing
                    }
                }
            }
            if (TextUtils.isEmpty(message)) return@observe
            //                showMessageError(message)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showHideLoading(it)
        }

    }

}