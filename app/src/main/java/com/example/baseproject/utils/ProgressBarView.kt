package com.example.baseproject.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.example.baseproject.R

object ProgressBarView {
    private lateinit var progressBar : Dialog

    fun showProgressBar(activity: Activity?) {
        progressBar = Dialog(activity!!)
        progressBar.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressBar.setCancelable(false)
        progressBar.setContentView(R.layout.layout_loading)
        progressBar.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        progressBar.show()
    }

    fun hideProgressBar() {
        progressBar.dismiss()
    }
}