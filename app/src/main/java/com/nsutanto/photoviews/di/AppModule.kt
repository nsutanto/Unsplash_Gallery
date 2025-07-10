package com.nsutanto.photoviews.di

import androidx.room.Room
import com.nsutanto.photoviews.BuildConfig
import com.nsutanto.photoviews.api.ApiService
import com.nsutanto.photoviews.api.IApiService
import com.nsutanto.photoviews.db.AppDatabase
import com.nsutanto.photoviews.repository.IPhotoRepository
import com.nsutanto.photoviews.repository.PhotoRemoteMediator
import com.nsutanto.photoviews.repository.PhotoRepository
import com.nsutanto.photoviews.viewmodel.PhotoDetailViewModel
import com.nsutanto.photoviews.viewmodel.PhotoGalleryViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.gson.gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                gson()
            }
        }
    }

    single<IApiService> {
        ApiService(
            client = get(),
            apiKey = BuildConfig.UNSPLASH_ACCESS_KEY
        )
    }
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "photos.db"
        ).build()
    }

    // DAO
    single { get<AppDatabase>().photoDao() }

    // RemoteMediator
    factory {
        PhotoRemoteMediator(
            api = get(),
            dao = get(),
            db = get()
        )
    }

    single { get<AppDatabase>().photoDao() }
    single<IPhotoRepository> { PhotoRepository(dao = get(), remoteMediator = get()) }
    viewModelOf(::PhotoGalleryViewModel)
    viewModelOf(::PhotoDetailViewModel)
}