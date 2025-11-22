package com.nsutanto.photoviews.api

import com.nsutanto.photoviews.model.ApiResponse

interface IApiService {

    suspend fun fetchAPI(): ApiResponse
}