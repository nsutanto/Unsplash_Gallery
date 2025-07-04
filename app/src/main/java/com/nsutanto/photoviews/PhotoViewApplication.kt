package com.nsutanto.photoviews

import android.app.Application
import android.os.StrictMode


class PhotoViewApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupStrictMode()
    }

    private fun setupStrictMode() {
        // Set up the StrictMode policy
        if (BuildConfig.DEBUG) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDialog()
                .build()
            StrictMode.setThreadPolicy(policy)

            val vmPolicy = StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build()
            StrictMode.setVmPolicy(vmPolicy)
        }
    }
}