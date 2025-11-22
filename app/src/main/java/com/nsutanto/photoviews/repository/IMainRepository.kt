package com.nsutanto.photoviews.repository

import com.nsutanto.photoviews.model.MainResponse
import kotlinx.coroutines.flow.StateFlow

interface IMainRepository {
    // Use flow
    val apiResponse: StateFlow<MainResponse?>

    suspend fun fetchApi()

}