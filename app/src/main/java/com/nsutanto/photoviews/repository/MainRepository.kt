package com.nsutanto.photoviews.repository

import com.nsutanto.photoviews.api.IApiService
import com.nsutanto.photoviews.model.MainResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class MainRepository(private val apiService: IApiService): IMainRepository {

    private val _apiResponse = MutableStateFlow<MainResponse?>(null)
    override val apiResponse: StateFlow<MainResponse?> = _apiResponse


    override suspend fun fetchApi() {
        withContext(Dispatchers.IO) {
            try {
                _apiResponse.value = apiService.fetchApi()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}