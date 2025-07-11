package com.nsutanto.photoviews.api

import com.nsutanto.photoviews.model.Photo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.isSuccess

class ApiService(
    private val client: HttpClient,
    private val apiKey: String
) : IApiService {

    companion object {
        const val PER_PAGE = 10
        private const val BASE_URL = "https://api.unsplash.com"
    }

    // Ideally we should have APIResponse that contain success, error, and data
    override suspend fun fetchPhotos(page: Int, perPage: Int): List<Photo> {
        //println("***** Fetch Photos: page=$page, perPage=$perPage *****")
        val response: HttpResponse = client.get("$BASE_URL/photos") {
            parameter("page", page)
            parameter("per_page", perPage)
            headers {
                append("Authorization", "Client-ID $apiKey")
            }
        }
        if (!response.status.isSuccess()) {
            // TODO: Should probably have an APIException that derived from Exception
            throw Exception("HTTP ${response.status.value}: ${response.status.description}")
        }

        return response.body()
    }
}

