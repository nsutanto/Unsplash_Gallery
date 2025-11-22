package com.nsutanto.photoviews.di

import com.nsutanto.photoviews.BuildConfig
import com.nsutanto.photoviews.api.ApiService
import com.nsutanto.photoviews.api.IApiService
import com.nsutanto.photoviews.repository.APIRepository
import com.nsutanto.photoviews.repository.IApiRepository
import com.nsutanto.photoviews.viewmodel.MainViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.gson.gson
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

    single<IApiRepository> { APIRepository(apiService = get()) }
    viewModelOf(::MainViewModel)
}