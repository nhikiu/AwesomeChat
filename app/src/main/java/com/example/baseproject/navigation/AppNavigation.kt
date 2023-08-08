package com.example.baseproject.navigation

import android.os.Bundle
import com.example.core.navigationComponent.BaseNavigator

interface AppNavigation : BaseNavigator {

    fun openSplashToHomeScreen(bundle: Bundle? = null)

    fun openSplashToLogInScreen(bundle: Bundle? = null)

    fun openLogInToSignUpScreen(bundle: Bundle? = null)

    fun openLogInToHomeScreen(bundle: Bundle? = null)

    fun openSignUpToHomeScreen(bundle: Bundle? = null)

    fun openSignUpToLogInScreen(bundle: Bundle? = null)

    fun openHomeToLogInScreen(bundle: Bundle? = null)

    fun openHomeToProfileDetail(bundle: Bundle? = null)

    fun openHomeToMessageScreen(bundle: Bundle? = null)

}