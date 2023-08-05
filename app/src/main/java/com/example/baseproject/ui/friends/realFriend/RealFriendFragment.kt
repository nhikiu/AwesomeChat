package com.example.baseproject.ui.friends.realFriend

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentRealFriendBinding
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.utils.DialogView
import com.example.baseproject.utils.ProgressBarView
import com.example.baseproject.utils.UIState
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RealFriendFragment : BaseFragment<FragmentRealFriendBinding, RealFriendViewModel>(R.layout.fragment_real_friend) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: RealFriendViewModel by viewModels()

    override fun getVM() = viewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val realFriendAdapter: RealFriendAdapter = RealFriendAdapter()

        binding.recyclerViewRealFriend.adapter = realFriendAdapter

        viewModel.getAllRealFriend{ state ->
            when(state){
                is UIState.Success -> ProgressBarView.hideProgressBar()
                is UIState.Loading -> ProgressBarView.showProgressBar(activity)
                is UIState.Failure -> {
                    ProgressBarView.hideProgressBar()
                    DialogView().showErrorDialog(
                        activity,
                        resources.getString(R.string.error),
                        resources.getString(R.string.error_unknown)
                    )
                }
            }
        }

        viewModel.realFriendListLiveData.observe(viewLifecycleOwner){
            realFriendAdapter.submitList(it.toMutableList())
        }
    }

}