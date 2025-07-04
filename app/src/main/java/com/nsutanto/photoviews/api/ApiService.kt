package com.nsutanto.photoviews.api

import com.nsutanto.photoviews.model.Photo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*

object ApiService {


    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun fetchPhotos(): List<Photo> {
        val response: HttpResponse = client.get("https://api.unsplash.com/photos") {
            // TODO: Move access key to a secure location
            headers {
                append("Authorization", "Client-ID 7rfF4QcN2PgI1UICVS8TVg-QA_W0xxKvfSUgLQZoqt8")
            }
        }
        return response.body()
    }
}
