package com.example.setting.ui.viewPager

import androidx.lifecycle.MutableLiveData
import com.example.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DemoViewPagerViewModel @Inject constructor() : BaseViewModel() {

    val isCanRequestPoint = MutableLiveData(false)

    fun canLoadData() {
        if (isCanRequestPoint.value == false) {
            isCanRequestPoint.value = true
        }
    }
}