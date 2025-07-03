package com.nsutanto.photoviews.repository

import com.nsutanto.photoviews.model.Photo


interface IPhotosRepository {
    suspend fun fetchPhotos(): List<Photo>
}