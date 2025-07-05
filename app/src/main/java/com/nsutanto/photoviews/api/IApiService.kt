package com.nsutanto.photoviews.api

import com.nsutanto.photoviews.api.ApiService.Companion.PER_PAGE
import com.nsutanto.photoviews.model.Photo

interface IApiService {
    suspend fun fetchPhotos(page: Int, perPage: Int = PER_PAGE): List<Photo>
}