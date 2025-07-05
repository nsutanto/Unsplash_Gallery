package com.nsutanto.photoviews.repository

import com.nsutanto.photoviews.model.Photo
import kotlinx.coroutines.flow.Flow

interface IPhotoRepository {
    suspend fun fetchPhotos(page: Int)

    val photoFlow: Flow<List<Photo>>
}