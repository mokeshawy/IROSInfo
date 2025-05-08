package com.irosinfo.application

import android.app.Application
import com.common.common_module.BuildConfig
import timber.log.Timber

class IrosApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        plantTimberTrees()
    }


    private fun plantTimberTrees() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}