package com.nsutanto.photoviews.repository

import com.nsutanto.photoviews.api.IApiService
import com.nsutanto.photoviews.db.PhotoDao
import com.nsutanto.photoviews.db.toEntity
import com.nsutanto.photoviews.db.toPhoto
import com.nsutanto.photoviews.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.flowOn

class PhotoRepository(
    private val api: IApiService,
    private val dao: PhotoDao
) : IPhotoRepository {


    override val photoFlow: Flow<List<Photo>> = dao.getAll().map { entities ->
        entities.map {
            it.toPhoto()
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun fetchPhotos(page: Int) {
        try {
            val newPhotos = api.fetchPhotos(page)
            withContext(Dispatchers.IO) {
                val entities = newPhotos.map { it.toEntity() }
                dao.insertAll(entities)
            }
        }  catch (e: Exception) {
            // Throw for now so that the ViewModel can handle it
            // Ideally we should have APIResponse that contain success, error, and data
            throw e
        }
    }
}
