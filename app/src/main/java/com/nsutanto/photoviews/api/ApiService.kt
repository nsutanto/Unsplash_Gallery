package com.nsutanto.photoviews.api

import com.nsutanto.photoviews.model.Photo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import com.nsutanto.photoviews.BuildConfig
import io.ktor.http.isSuccess

object ApiService {
    const val PER_PAGE = 10
    private const val BASE_URL = "https://api.unsplash.com"

    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun fetchPhotos(page: Int, perPage: Int = PER_PAGE): List<Photo> {
        val response: HttpResponse = client.get("$BASE_URL/photos") {
            parameter("page", page)
            parameter("per_page", perPage)
            headers {
                append("Authorization", "Client-ID ${BuildConfig.UNSPLASH_ACCESS_KEY}")
            }
        }
        if (!response.status.isSuccess()) {
            // TODO: Probably need to implement API Exception for specific API errors
            throw Exception("HTTP ${response.status.value}: ${response.status.description}")
        }
        // TODO: Need to return APIResponse object that contains the data, API responses / error, etc
        return response.body()
    }
}

