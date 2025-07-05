package com.nsutanto.photoviews

import android.app.Application
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
    }
}