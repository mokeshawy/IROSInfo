package com.irosinfo.application

import android.app.Application
import com.common.common_module.BuildConfig
import com.irosinfo.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class IrosApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        handleStartKoinApplication()
        plantTimberTrees()

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
}