package com.example.baseproject.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.view.View
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentProfileBinding
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.ui.chats.ActionState
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.DialogView
import com.example.baseproject.utils.ProgressBarView
import com.example.baseproject.utils.UIState
import com.example.core.base.fragment.BaseFragment
import com.example.core.utils.setLanguage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentProfileBinding, ProfileViewModel>(R.layout.fragment_profile) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: ProfileViewModel by viewModels()
    override fun getVM() = viewModel

    private val alert = DialogView()

    override fun bindingStateView() {
        super.bindingStateView()
        observer()
        viewModel.actionFriend.observe(viewLifecycleOwner) {
            when (it) {
                is ActionState.Finish -> binding.progressBar.visibility = View.GONE
                else -> binding.progressBar.visibility = View.VISIBLE
            }
        }

        viewModel.getCurrentUser()
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnLogOut.setOnClickListener {
            viewModel.logoutUser()
        }

        binding.btnEdit.setOnClickListener {
            appNavigation.openHomeToProfileDetail()
        }

        binding.ivChangeLanguage.setOnClickListener {
            val listItems = arrayOf(
                resources.getString(R.string.language_en), resources.getString(R.string.language_vi)
            )
            val mBuilder = AlertDialog.Builder(requireContext())
            mBuilder.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->
                binding.tvLanguage.text = listItems[i]
                dialogInterface.dismiss()
            }

            val mDialog = mBuilder.create()
            mDialog.show()
            val language = binding.tvLanguage.text
            if (language == resources.getString(R.string.language_vi)) {
                changeLanguage("vi")
            } else {
                changeLanguage("en")
            }
        }
    }

    private fun changeLanguage(language: String) {
        requireContext().setLanguage(language)
        viewModel.setLanguage(language)
    }

    private fun observer() {
        viewModel.signout.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIState.Loading -> {
                    ProgressBarView.showProgressBar(activity)
                }
                is UIState.Failure -> {
                    context?.let {
                        state.error?.let { it1 ->
                            alert.showErrorDialog(
                                activity, it.getString(R.string.error), it1
                            )
                        }
                    }
                    ProgressBarView.hideProgressBar()
                }
                is UIState.Success -> {
                    appNavigation.openHomeToLogInScreen()
                    context?.let {
                        val sharePref =
                            it.getSharedPreferences(Constants.ISLOGIN, Context.MODE_PRIVATE)
                        val editor = sharePref.edit()
                        editor.putBoolean(Constants.ISLOGIN, false)
                        editor.apply()
                    }
                    ProgressBarView.hideProgressBar()
                    activity?.viewModelStore?.clear()
                    viewModelStore.clear()
                }
            }
        }

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            binding.tvName.text = user.name
            binding.tvEmail.text = user.email

            if (user.avatar != null && user.avatar.isNotEmpty()) {
                Glide.with(this).load(user.avatar)
                    .placeholder(R.drawable.ic_avatar_default)
                    .into(binding.ivAvatar)

                Glide.with(this).load(user.avatar)
                    .placeholder(R.drawable.ic_avatar_default)
                    .into(binding.ivAvatarLarge)
            }
        }
    }
}