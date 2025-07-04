package com.nsutanto.photoviews.repository

import com.nsutanto.photoviews.api.ApiService
import com.nsutanto.photoviews.db.PhotoDao
import com.nsutanto.photoviews.db.toEntity
import com.nsutanto.photoviews.db.toPhoto
import com.nsutanto.photoviews.model.Photo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PhotoRepository(
    private val api: ApiService,
    private val dao: PhotoDao
) : IPhotoRepository {

    private val _photoFlow = MutableStateFlow<List<Photo>>(emptyList())
    override val photoFlow: StateFlow<List<Photo>> = _photoFlow

    init {
        // Load cached photos first
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val cachedPhotos = dao.getAll().map { it.toPhoto() }
                _photoFlow.value = cachedPhotos
            }
        } catch (e: Exception) {
            // Handle any exceptions that occur while fetching cached photos
            _photoFlow.value = emptyList()
        }
    }

    override suspend fun fetchPhotos(page: Int) {
        try {
            val newPhotos = api.fetchPhotos(page)
            withContext(Dispatchers.IO) {
                val entities = newPhotos.map { it.toEntity() }
                dao.insertAll(entities)
            }

            _photoFlow.value += newPhotos
        } catch (e: Exception) {
            // TODO: Handle all exception for now, maybe need a separate API exception or other error
            val cachedPhotos = withContext(Dispatchers.IO) {
                dao.getAll().map { it.toPhoto() }
            }
            _photoFlow.value = cachedPhotos
        }
    }
}
