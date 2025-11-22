package com.nsutanto.photoviews.api

import com.nsutanto.photoviews.model.MainResponse

interface IApiService {

    suspend fun fetchApi(): MainResponse
}