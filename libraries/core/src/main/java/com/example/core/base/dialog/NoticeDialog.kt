package com.example.core.base.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.core.R

const val NOTICE_DIALOG_FRAGMENT = "NOTICE_DIALOG_FRAGMENT"

class NoticeDialog private constructor() : DialogFragment() {

    private var confirmDialogListener: NoticeDialogListener? = null

    fun setDialogListener(confirmDialogListener: NoticeDialogListener) {
        this.confirmDialogListener = confirmDialogListener
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment != null && parentFragment is NoticeDialogListener) {
            setDialogListener(parentFragment as NoticeDialogListener)
            return
        }

        if (context is NoticeDialogListener) {
            setDialogListener(context as NoticeDialogListener)
            return
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(NOTICE_DIALOG_TITLE) ?: ""
        val type = arguments?.getInt(NOTICE_DIALOG_TYPE)
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setPositiveButton(R.string.ok) { _, _ ->
                confirmDialogListener?.onClickOk(type)
            }
        return builder.create()
    }

    companion object {
        private const val NOTICE_DIALOG_TITLE = "NOTICE_DIALOG_TITLE"
        private const val NOTICE_DIALOG_TYPE = "NOTICE_DIALOG_TYPE"

        fun getInstance(title: String, type: Int? = null): NoticeDialog {
            val bundle = Bundle()
            bundle.putString(NOTICE_DIALOG_TITLE, title)
            type?.let {
                bundle.putInt(NOTICE_DIALOG_TYPE, type)
            }
            return NoticeDialog().apply { arguments = bundle }
        }
    }
}

interface NoticeDialogListener {
    fun onClickOk(type: Int?)
}