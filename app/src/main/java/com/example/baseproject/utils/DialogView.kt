package com.example.baseproject.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.baseproject.R

class DialogView {
    fun showErrorDialog(activity: Activity?, title: String, body: String) {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val tvBody = dialog.findViewById<TextView>(R.id.tv_body)

        tvTitle.text = title
        tvBody.text = body

        val btnCancel = dialog.findViewById<Button>(R.id.btn_confirm)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}