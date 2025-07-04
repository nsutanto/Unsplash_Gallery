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
                if (cachedPhotos.isNotEmpty()) {
                    _photoFlow.value = cachedPhotos
                }
            }
        } catch (e: Exception) {
            // Handle any exceptions that occur while fetching cached photos
            _photoFlow.value = emptyList()
        }
    }

    override suspend fun fetchPhotos(page: Int) {
        try {
            println("***** Fetch Photos Page: $page")
            val newPhotos = api.fetchPhotos(page)
            val uniqueNewPhotos = newPhotos.filterNot { newPhoto ->
                _photoFlow.value.any { it.id == newPhoto.id }
            }

            withContext(Dispatchers.IO) {
                val entities = uniqueNewPhotos.map { it.toEntity() }
                dao.insertAll(entities)
            }

            // Append only unique new photos to the current list
            _photoFlow.value += uniqueNewPhotos
        } catch (e: Exception) {
            println("***** Get Exception: ${e.message}")
            // TODO: Handle all exception for now, maybe need a separate API exception or other error
            val cachedPhotos = withContext(Dispatchers.IO) {
                dao.getAll().map { it.toPhoto() }
            }
            _photoFlow.value = cachedPhotos
        }
    }
}
