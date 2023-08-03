package com.example.baseproject.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.example.baseproject.databinding.CustomDialogBinding

class DialogView {
    fun showErrorDialog(activity: Activity?, title: String, body: String) {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        val binding = CustomDialogBinding.inflate(dialog.layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.tvTitle.text = title
        binding.tvBody.text = body

        binding.btnConfirm.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}