package com.nsutanto.photoviews.di

import androidx.room.Room
import com.nsutanto.photoviews.api.ApiService
import com.nsutanto.photoviews.db.AppDatabase
import com.nsutanto.photoviews.repository.IPhotoRepository
import com.nsutanto.photoviews.repository.PhotoRepository
import com.nsutanto.photoviews.viewmodel.PhotoDetailViewModel
import com.nsutanto.photoviews.viewmodel.PhotoGalleryViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { ApiService }
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "photos.db"
        ).build()
    }
    single { get<AppDatabase>().photoDao() }
    single<IPhotoRepository> { PhotoRepository(api = get(), dao = get()) }
    viewModelOf(::PhotoGalleryViewModel)
    viewModelOf(::PhotoDetailViewModel)
}