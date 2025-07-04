package com.nsutanto.photoviews

import android.app.Application
import android.os.StrictMode
import com.nsutanto.photoviews.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin


class PhotoViewApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PhotoViewApplication)
            modules(appModule)
        }
        //setupStrictMode()
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