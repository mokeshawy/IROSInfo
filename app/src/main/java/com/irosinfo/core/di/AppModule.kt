package com.irosinfo.core.di

import com.crash_reporting.crash_reporting_module.crash_reporting.di.crashReportingModule
import com.irosinfo.ui_component.custom_progress_dialog.di.progressDialogModule
import com.storage.storage_module.storage_manager.di.sharedPreferenceModule
import org.koin.dsl.module

val appModule = module {
    includes(progressDialogModule)
    includes(crashReportingModule)
    includes(sharedPreferenceModule)
}