package com.nsutanto.photoviews.repository

interface IPhotosRepository {
    suspend fun fetchPhotos(page: Int)
}