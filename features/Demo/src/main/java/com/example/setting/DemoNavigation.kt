package com.example.setting

import android.os.Bundle
import com.example.core.navigationComponent.BaseNavigator

interface DemoNavigation : BaseNavigator {

    fun openDemoViewPager(bundle: Bundle? = null)
}