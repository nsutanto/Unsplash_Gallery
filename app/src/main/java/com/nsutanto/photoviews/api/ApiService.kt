package com.nsutanto.photoviews.api

import com.nsutanto.photoviews.model.MainResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.isSuccess

class ApiService(
    private val client: HttpClient,
) : IApiService {

    companion object {
        private const val BASE_URL = "https://dummyjson.com/products"
    }

    override suspend fun fetchApi(): MainResponse {
        val response: HttpResponse = client.get(BASE_URL)

        if (!response.status.isSuccess()) {
            throw Exception("HTTP ${response.status.value}: ${response.status.description}")
        }
        return response.body()
    }
}

