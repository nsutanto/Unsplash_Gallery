package com.nsutanto.photoviews.di

import com.nsutanto.photoviews.repository.IPhotoRepository
import com.nsutanto.photoviews.repository.PhotoRepository
import com.nsutanto.photoviews.viewmodel.PhotoDetailViewModel
import com.nsutanto.photoviews.viewmodel.PhotoGalleryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<IPhotoRepository> { PhotoRepository() } // Shared repository
    viewModel { PhotoGalleryViewModel(get()) }
    viewModel { PhotoDetailViewModel(get()) }
}