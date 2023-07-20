package com.example.baseproject.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentHomeBinding
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.DialogView
import com.example.baseproject.utils.ProgressBarView
import com.example.baseproject.utils.UIState
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {

    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: HomeViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
    }

    override fun getVM() = viewModel

    private val alert = DialogView()

    override fun bindingStateView() {
        super.bindingStateView()
        observer()
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnLogOut.setOnClickListener {
            viewModel.logoutUser()
        }
    }

    private fun observer() {
        viewModel.signout.observe(viewLifecycleOwner){state ->
            when(state) {
                is UIState.Loading -> {
                    ProgressBarView.showProgressBar(activity)
                }
                is UIState.Failure -> {
                    alert.showErrorDialog(activity, "Log out error", state.error!!)
                    ProgressBarView.hideProgressBar()
                }
                is UIState.Success -> {
                    Toast.makeText(context, "Logout success", Toast.LENGTH_LONG).show()
                    appNavigation.openHomeToLogInScreen()
                    val sharePref = context?.getSharedPreferences(Constants.ISLOGIN, Context.MODE_PRIVATE)
                    val editor = sharePref?.edit()
                    editor?.putBoolean(Constants.ISLOGIN, false)
                    editor?.apply()
                    ProgressBarView.hideProgressBar()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.tag("HomeFragment").d("A   " + "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("HomeFragment").d("A   " + "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.tag("HomeFragment").d("A   " + "onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.tag("HomeFragment").d("A   " + "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        Timber.tag("HomeFragment").d("A   " + "onStart")
        super.onStart()
    }

    override fun onResume() {
        Timber.tag("VietBH").d("A   " + "onResume")
        super.onResume()
    }

    override fun onPause() {
        Timber.tag("HomeFragment").d("A   " + "onPause")
        super.onPause()
    }

    override fun onStop() {
        Timber.tag("HomeFragment").d("A   " + "onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Timber.tag("HomeFragment").d("A   " + "onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Timber.tag("HomeFragment").d("A   " + "onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Timber.tag("HomeFragment").d("A   " + "onCreate")
        super.onDetach()
    }


}