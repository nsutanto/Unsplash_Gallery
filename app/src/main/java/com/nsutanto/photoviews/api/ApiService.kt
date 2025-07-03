package com.nsutanto.photoviews.api

import com.nsutanto.photoviews.model.Photo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*

object ApiService {
    val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun fetchPhotos(): List<Photo> {
        val response: HttpResponse = client.get("https://fetch-hiring.s3.amazonaws.com/hiring.json")
        return response.body()
    }
}
