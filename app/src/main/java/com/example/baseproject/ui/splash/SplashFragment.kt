package com.example.baseproject.ui.splash

import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentSplashBinding
import com.example.baseproject.navigation.AppNavigation
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment :
    BaseFragment<FragmentSplashBinding, SplashViewModel>(R.layout.fragment_splash) {

    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: SplashViewModel by viewModels()

    override fun getVM() = viewModel

    override fun bindingAction() {
        super.bindingAction()

        viewModel.actionSPlash.observe(viewLifecycleOwner) {
            appNavigation.openSplashToLogInScreen()
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.splashTitle.observe(viewLifecycleOwner) {
        }
    }
}