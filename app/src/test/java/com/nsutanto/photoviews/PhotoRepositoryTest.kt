package com.nsutanto.photoviews

import com.nsutanto.photoviews.api.IApiService
import com.nsutanto.photoviews.db.PhotoDao
import com.nsutanto.photoviews.db.PhotoEntity
import com.nsutanto.photoviews.model.Photo
import com.nsutanto.photoviews.model.PhotoUrls
import com.nsutanto.photoviews.model.PhotoUser
import com.nsutanto.photoviews.repository.PhotoRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class PhotoRepositoryTest {

    private val api = mockk<IApiService>()
    private val dao = mockk<PhotoDao>()
    private lateinit var repository: PhotoRepository

    private val testPhotos = listOf(
        Photo(id = "1", urls = PhotoUrls("url1"), user = PhotoUser("user1"), description = "desc1"),
        Photo(id = "2", urls = PhotoUrls("url2"), user = PhotoUser("user2"), description = "desc2")
    )
    private val testEntities = listOf(
        PhotoEntity(id = "1", url = "url1", username = "user1", description = "desc1"),
        PhotoEntity(id = "2", url = "url2", username = "user2", description = "desc2")
    )

    @Before
    fun setup() {
        coEvery { dao.getAll() } returns flowOf(testEntities)
        repository = PhotoRepository(api, dao)
    }

    @Test
    fun `fetchPhotos inserts photos from API`() = runTest {
        coEvery { api.fetchPhotos(1) } returns testPhotos
        coEvery { dao.insertAll(any()) } just Runs

        repository.fetchPhotos(1)

        coVerify { dao.insertAll(match {
            it.size == 2 && it[0].id == "1" && it[1].id == "2" })
        }
    }

    @Test(expected = Exception::class)
    fun `fetchPhotos throws on API error`() = runTest {
        coEvery { api.fetchPhotos(1) } throws Exception("API error")


        runTest {
            repository.fetchPhotos(1)
        }

    }

    @Test
    fun `photoFlow emits mapped photos from dao`() = runTest {
        val result = repository.photoFlow // This is a Flow<List<Photo>>
        result.collect { photos ->
            assertEquals(2, photos.size)
            assertEquals("1", photos[0].id)
            assertEquals("2", photos[1].id)
            return@collect // Only check first emission
        }
    }
}