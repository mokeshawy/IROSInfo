package com.irosinfo.ui_component.custom_progress_dialog.di

import android.app.Activity
import com.irosinfo.ui_component.custom_progress_dialog.ProgressDialog
import org.koin.dsl.module


val progressDialogModule = module {
    factory { (activity: Activity) ->
        ProgressDialog(activity)
    }
}