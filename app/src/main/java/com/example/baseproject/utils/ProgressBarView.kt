package com.example.baseproject.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.example.baseproject.R
import java.lang.ref.WeakReference

object ProgressBarView {
    private var weakProgressBar : WeakReference<Dialog>? = null

    fun showProgressBar(activity: Activity?) {
        activity?.let {
            val progressBar = Dialog(it)
            progressBar.requestWindowFeature(Window.FEATURE_NO_TITLE)
            progressBar.setCancelable(false)
            progressBar.setContentView(R.layout.layout_loading)
            progressBar.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            progressBar.show()

            weakProgressBar = WeakReference(progressBar)

        }
    }

    fun hideProgressBar() {
        weakProgressBar?.get()?.apply {
            dismiss()
            cancel()
        }
        weakProgressBar = null
    }
}