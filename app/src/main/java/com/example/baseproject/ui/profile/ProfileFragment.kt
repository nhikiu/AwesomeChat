package com.example.baseproject.ui.profile

import android.content.Context
import android.util.Log
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentProfileBinding
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.DialogView
import com.example.baseproject.utils.ProgressBarView
import com.example.baseproject.utils.UIState
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>(R.layout.fragment_profile) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: ProfileViewModel by viewModels()
    override fun getVM() = viewModel

    private val alert = DialogView()

    override fun bindingStateView() {
        super.bindingStateView()
        observer()
        viewModel.getCurrentUser()
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
                    context?.let { alert.showErrorDialog(activity, it.getString(R.string.error), state.error!!) }
                    ProgressBarView.hideProgressBar()
                }
                is UIState.Success -> {
                    appNavigation.openHomeToLogInScreen()
                    context?.let {
                        val sharePref = it.getSharedPreferences(Constants.ISLOGIN, Context.MODE_PRIVATE)
                        val editor = sharePref.edit()
                        editor.putBoolean(Constants.ISLOGIN, false)
                        editor.apply()
                    }
                    ProgressBarView.hideProgressBar()
                }
            }
        }

        viewModel.currentUser.observe(viewLifecycleOwner){user ->
            binding.tvName.text = user.name
            binding.tvEmail.text = user.email
        }
    }
}