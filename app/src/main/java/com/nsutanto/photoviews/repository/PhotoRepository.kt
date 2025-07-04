package com.nsutanto.photoviews.repository

import com.nsutanto.photoviews.api.ApiService
import com.nsutanto.photoviews.model.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PhotoRepository : IPhotosRepository {

    // Backing state to emit the photo list
    private val _photoFlow = MutableStateFlow<List<Photo>>(emptyList())

    // Public immutable flow
    val photoFlow: StateFlow<List<Photo>> = _photoFlow

    override suspend fun fetchPhotos(page: Int) {
        // TODO: Handle Network Errors
        val newPhotos = ApiService.fetchPhotos(page = page)
        _photoFlow.value += newPhotos

    }
}