package com.example.baseproject.navigation

import android.os.Bundle
import com.example.baseproject.R
import com.example.core.navigationComponent.BaseNavigatorImpl
import com.example.setting.DemoNavigation
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class AppNavigatorImpl @Inject constructor() : BaseNavigatorImpl(),
    AppNavigation, DemoNavigation {

    override fun openSplashToHomeScreen(bundle: Bundle?) {
        openScreen(R.id.action_splashFragment_to_homeFragment)
    }

    override fun openSplashToLogInScreen(bundle: Bundle?) {
        openScreen(R.id.action_splashFragment_to_loginFragment)
    }

    override fun openSignUpToLogInScreen(bundle: Bundle?) {

    }

    override fun openSignUpToHomeScreen(bundle: Bundle?) {
        openScreen(R.id.action_signupFragment_to_homeFragment, bundle)
    }

    override fun openHomeToLogInScreen(bundle: Bundle?) {
        openScreen(R.id.action_homeFragment_to_loginFragment)
    }



    override fun openLogInToSignUpScreen(bundle: Bundle?) {
        openScreen(R.id.action_loginFragment_to_signupFragment)
    }

    override fun openLogInToHomeScreen(bundle: Bundle?) {
        openScreen(R.id.action_loginFragment_to_homeFragment)
    }

    override fun openDemoViewPager(bundle: Bundle?) {
    }

    override fun openHomeToProfileDetail(bundle: Bundle?) {
        openScreen(R.id.action_homeFragment_to_profileDetailFragment)
    }
}