package com.example.baseproject.ui.splash

import android.content.Context
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentSplashBinding
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.utils.Constants
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
            if (it == SplashActionState.Finish) {
                val sharePreferences = context?.getSharedPreferences(Constants.ISLOGIN, Context.MODE_PRIVATE)
                var isLogIn: Boolean
                sharePreferences?.let {
                    isLogIn = it.getBoolean(Constants.ISLOGIN, false)
                    if (isLogIn) {
                        appNavigation.openSplashToHomeScreen()
                    }
                    else {
                        appNavigation.openSplashToLogInScreen()
                    }
                }
            }
        }

    }
}