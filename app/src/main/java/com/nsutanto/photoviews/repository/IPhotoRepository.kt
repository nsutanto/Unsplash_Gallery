package com.nsutanto.photoviews.repository

import com.nsutanto.photoviews.model.Photo
import kotlinx.coroutines.flow.StateFlow

interface IPhotoRepository {
    suspend fun fetchPhotos(page: Int)

    val photoFlow: StateFlow<List<Photo>>
}