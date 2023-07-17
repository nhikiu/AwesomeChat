package com.example.core.utils.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.core.R
import java.lang.ref.WeakReference

class LoadingDialog private constructor(private var mActivity: Activity?) {

    private var isShow = false
    private lateinit var dialog: AlertDialog

    fun show() {
        if (mActivity != null && mActivity?.isFinishing == false) {
            if (!isShow) {
                isShow = true
                dialog.show()
            }
        }
    }

    fun hidden() {
        if (isShow && dialog.isShowing) {
            isShow = false
            dialog.dismiss()
        }
    }

    fun destroyLoadingDialog() {
        mActivity = null
        if (instance != null) {
            instance!!.dialog.dismiss()
        }
        instance = null
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: LoadingDialog? = null

        fun getInstance(mActivity: Activity): LoadingDialog? {
            if (instance == null) {
                instance = LoadingDialog(WeakReference(mActivity).get())
            }
            return instance
        }
    }

    init {
        if (mActivity != null && !isShow) {
            val dialogBuilder =
                AlertDialog.Builder(mActivity)
            val li = LayoutInflater.from(mActivity)
            val dialogView = li.inflate(R.layout.layout_loading, null)
            dialogBuilder.setView(dialogView)
            dialogBuilder.setCancelable(false)
            dialog = dialogBuilder.create()
            if (dialog.window != null) {
                dialog.window!!
                    .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
        }
    }
}