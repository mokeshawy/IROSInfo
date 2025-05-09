package com.irosinfo.ui_component.custom_progress_dialog

import android.app.Activity
import android.app.AlertDialog
import com.irosinfo.databinding.LayoutProgressDialogBinding
import com.utils.utils_module.CommonUtils.setLayoutParams


class ProgressDialog(private val activity: Activity) {

    private val binding by lazy { LayoutProgressDialogBinding.inflate(activity.layoutInflater) }
    private var dialog: AlertDialog? = null

    init {
        buildAlertDialogView()
    }

    private fun buildAlertDialogView(): AlertDialog? {
        val builder = AlertDialog.Builder(activity)
        builder.setView(binding.root)
        dialog = builder.create()
        dialog?.let { setLayoutParams(it) }
        dialog?.setCancelable(false)
        return dialog
    }

    fun show() {
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}