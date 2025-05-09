package com.irosinfo.application

import com.bases.bases_module.base_application.BaseApplication
import com.common.common_module.BuildConfig
import com.crash_reporting.crash_reporting_module.crash_reporting.CrashReportingHandler
import com.crash_reporting.crash_reporting_module.crash_reporting.crash_reporting_tools.FirebaseCrashReportingTool
import com.google.firebase.FirebaseApp
import com.irosinfo.core.di.appModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class IrosApplication : BaseApplication() {


    private val firebaseCrashReportingTool: FirebaseCrashReportingTool by inject()

    override fun onCreate() {
        super.onCreate()
        handleStartKoinApplication()
        plantTimberTrees()
        initFirebaseApp()

    }


    private fun handleStartKoinApplication() = startKoin {
        androidLogger()
        androidContext(this@IrosApplication)
        modules(appModule)

    }

    private fun plantTimberTrees() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initFirebaseApp() {
        FirebaseApp.initializeApp(this)
    }

    override fun addCrashReportingTools(crashReportingHandler: CrashReportingHandler?) {
        if (this::firebaseCrashReportingTool.isLateinit) return
        crashReportingHandler?.registerCrashReportingTool(firebaseCrashReportingTool)
    }

    override fun getRemoteDebuggerPort() = 5055
}