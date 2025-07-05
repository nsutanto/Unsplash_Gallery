package com.nsutanto.photoviews

import com.google.gson.Gson
import com.nsutanto.photoviews.api.ApiService
import com.nsutanto.photoviews.model.Photo
import com.nsutanto.photoviews.model.PhotoUrls
import com.nsutanto.photoviews.model.PhotoUser
import io.ktor.client.*
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.*
import io.ktor.serialization.gson.gson
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ApiServiceTest {

    @Test
    fun `fetchPhotos returns list of photos on success`(): Unit = runTest {
        var capturedUrl: Url? = null
        val mockPhotos = listOf(
            Photo(
                id = "1",
                urls = PhotoUrls("url1"),
                user = PhotoUser("user1"),
                description = "description1"
            ),
            Photo(
                id = "2",
                urls = PhotoUrls("url2"),
                user = PhotoUser("user2"),
                description = "description2"
            )
        )
        val jsonResponse = Gson().toJson(mockPhotos)

        val mockEngine = MockEngine { request ->
            capturedUrl = request.url
            respond(
                content = ByteReadChannel(jsonResponse),
                status = HttpStatusCode.OK,
                headers = headersOf("Content-Type" to
                        listOf(ContentType.Application.Json.toString()))
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                gson()
            }
        }

        val service = ApiService(client, apiKey = "fake-api-key")
        val result = service.fetchPhotos(page = 2)

        assertEquals("2", capturedUrl?.parameters?.get("page"))
        assertEquals(2, result.size)
        assertEquals("1", result[0].id)
        assertEquals("url1", result[0].urls?.regular)
        assertEquals("user1", result[0].user?.username)
        assertEquals("2", result[1].id)
        assertEquals("url2", result[1].urls?.regular)
        assertEquals("user2", result[1].user?.username)
    }

    @Test(expected = Exception::class)
    fun `fetchPhotos throws exception on HTTP error`(): Unit = runTest {
        val mockEngine = MockEngine {
            respond(
                content = "Internal Server Error",
                status = HttpStatusCode.InternalServerError,
                headers = headersOf("Content-Type" to listOf(ContentType.Text.Plain.toString()))
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                gson()
            }
        }

        val apiService = ApiService(client, apiKey = "fake-key")

        // This line should throw
        apiService.fetchPhotos(1, 10)
    }
}

