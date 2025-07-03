package com.nsutanto.photoviews.repository

import com.nsutanto.photoviews.api.ApiService
import com.nsutanto.photoviews.model.Photo

class PhotoRepository : IPhotosRepository {
    override suspend fun fetchPhotos(): List<Photo> {


        // TODO
        return mutableListOf<Photo>()
        /*
        val result = ApiService.fetchPhotos()
        val filtered = result.filter { !it.name.isNullOrBlank() }
            .sortedWith(compareBy({ it.listId }, { it.name }))

        return filtered.groupBy { it.listId }
            .map { GroupedItem(it.key, it.value) }
            .sortedBy { it.listId }

         */
    }
}