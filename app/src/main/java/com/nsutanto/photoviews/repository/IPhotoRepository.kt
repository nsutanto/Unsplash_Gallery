package com.nsutanto.photoviews.repository

interface IPhotoRepository {
    suspend fun fetchPhotos(page: Int)
}