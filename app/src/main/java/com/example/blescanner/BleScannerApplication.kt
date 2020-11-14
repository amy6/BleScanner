package com.example.blescanner

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

class BleScannerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG)
            Timber.plant(DebugTree())
    }
}