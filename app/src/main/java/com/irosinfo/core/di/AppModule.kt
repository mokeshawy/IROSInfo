package com.irosinfo.core.di

import com.irosinfo.ui_component.custom_progress_dialog.di.progressDialogModule
import org.koin.dsl.module

val appModule = module {
    includes(progressDialogModule)
}