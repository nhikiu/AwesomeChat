package com.example.core.base.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.core.R

const val CONFIRM_DIALOG_FRAGMENT = "ConfirmDialogFragment"

class ConfirmDialogFragment private constructor() : DialogFragment() {

    private var confirmDialogListener: ConfirmDialogListener? = null

    fun setDialogListener(confirmDialogListener: ConfirmDialogListener) {
        this.confirmDialogListener = confirmDialogListener
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment != null && parentFragment is ConfirmDialogListener) {
            setDialogListener(parentFragment as ConfirmDialogListener)
            return
        }

        if (context is ConfirmDialogListener) {
            setDialogListener(context as ConfirmDialogListener)
            return
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(CONFIRM_DIALOG_TITLE) ?: ""
        val content = arguments?.getString(CONFIRM_DIALOG_CONTENT) ?: ""
        val type = arguments?.getInt(CONFIRM_DIALOG_TYPE)
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMessage(content)
            .setPositiveButton(R.string.ok) { _, _ ->
                confirmDialogListener?.onClickOk(type)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                confirmDialogListener?.onClickCancel(type)
            }
        return builder.create()
    }

    companion object {
        private const val CONFIRM_DIALOG_TITLE = "CONFIRM_DIALOG_TITLE"
        private const val CONFIRM_DIALOG_CONTENT = "CONFIRM_DIALOG_CONTENT"
        private const val CONFIRM_DIALOG_TYPE = "CONFIRM_DIALOG_TYPE"

        fun getInstance(title: String, content: String, type: Int? = null): ConfirmDialogFragment {
            val bundle = Bundle()
            bundle.putString(CONFIRM_DIALOG_TITLE, title)
            bundle.putString(CONFIRM_DIALOG_CONTENT, content)
            type?.let {
                bundle.putInt(CONFIRM_DIALOG_TYPE, type)
            }
            return ConfirmDialogFragment().apply { arguments = bundle }
        }
    }
}

interface ConfirmDialogListener {
    fun onClickOk(type: Int?)
    fun onClickCancel(type: Int?)
}