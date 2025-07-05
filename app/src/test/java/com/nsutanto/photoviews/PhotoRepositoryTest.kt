package com.nsutanto.photoviews

import app.cash.turbine.test
import com.nsutanto.photoviews.api.IApiService
import com.nsutanto.photoviews.db.PhotoDao
import com.nsutanto.photoviews.db.PhotoEntity
import com.nsutanto.photoviews.model.Photo
import com.nsutanto.photoviews.model.PhotoUrls
import com.nsutanto.photoviews.model.PhotoUser
import com.nsutanto.photoviews.repository.PhotoRepository
import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoRepositoryTest {

    private val api = mockk<IApiService>()
    private val dao = mockk<PhotoDao>()
    private lateinit var repository: PhotoRepository

    private val cachedEntities = listOf(
        PhotoEntity(id = "1", url = "url1", username = "user1", description = "desc1"),
        PhotoEntity(id = "2", url = "url2", username = "user2", description = "desc2")
    )

    private val testPhotosPage1 = listOf(
        Photo(id = "1", urls = PhotoUrls("url1"), user = PhotoUser("user1"), description = "desc1"),
        Photo(id = "2", urls = PhotoUrls("url2"), user = PhotoUser("user2"), description = "desc2")
    )
    private val testPhotosPage2 = listOf( // has same photo id 2
        Photo(id = "2", urls = PhotoUrls("url2"), user = PhotoUser("user2"), description = "desc2"),
        Photo(id = "3", urls = PhotoUrls("url3"), user = PhotoUser("user3"), description = "desc3")
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        coEvery { dao.getAll() } returns emptyList()
        repository = PhotoRepository(api = api, dao = dao)
    }

    @Test
    fun `fetchPhotos should update photoFlow if db is empty`() = runTest {
        coEvery { dao.getAll() } returns emptyList()
        coEvery { api.fetchPhotos(1) } returns testPhotosPage1
        coEvery { dao.insertAll(any()) } just Runs

        repository.fetchPhotos(1)

        repository.photoFlow.test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("1", result[0].id)
            assertEquals("2", result[1].id)
        }
        coVerify(exactly = 1) {
            dao.insertAll(match { entities ->
                entities.size == 2 && entities[0].id == "1" && entities[1].id == "2"
            })
        }
    }

    @Test
    fun `fetchPhotos should only insert unique photos`() = runTest {
        coEvery { dao.getAll() } returns emptyList()
        // Page 1: photos 1 and 2
        coEvery { api.fetchPhotos(1) } returns testPhotosPage1
        coEvery { api.fetchPhotos(2) } returns testPhotosPage2
        coEvery { dao.insertAll(any()) } just Runs

        repository.fetchPhotos(1)

        repository.photoFlow.test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.any { it.id == "1" })
            assertTrue(result.any { it.id == "2" })
        }

        repository.fetchPhotos(2)
        repository.photoFlow.test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertTrue(result.any { it.id == "1" })
            assertTrue(result.any { it.id == "2" })
            assertTrue(result.any { it.id == "3" })
            cancelAndIgnoreRemainingEvents()
        }

        // First call: insert photos 1 and 2
        coVerify(ordering = Ordering.ORDERED) {
            dao.insertAll(match { it.map { e -> e.id } == listOf("1", "2") })
        }

        // Second call: only insert unique (photo 3)
        coVerify {
            dao.insertAll(match { it.size == 1 && it[0].id == "3" })
        }
    }

    @Test
    fun `fetchPhotos should fallback to cache when API fails`() = runTest {

        coEvery { dao.getAll() } returns cachedEntities
        coEvery { api.fetchPhotos(1) } throws Exception("API error")

        repository.fetchPhotos(1)

        repository.photoFlow.test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("1", result[0].id)
            assertEquals("2", result[1].id)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(atLeast = 1) { dao.getAll() }
        coVerify(exactly = 0) { dao.insertAll(any()) }
    }


    @Test
    fun `fetchPhotos should add only unique photos to photoFlow`() = runTest {
        coEvery { dao.getAll() } returns cachedEntities
        coEvery { api.fetchPhotos(2) } returns testPhotosPage2
        coEvery { dao.insertAll(any()) } just Runs

        repository = PhotoRepository(api = api, dao = dao)

        advanceUntilIdle()

        repository.fetchPhotos(2)

        repository.photoFlow.test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertTrue(result.any { it.id == "1" })
            assertTrue(result.any { it.id == "2" })
            assertTrue(result.any { it.id == "3" })
            cancelAndIgnoreRemainingEvents()
        }

        // Verify: only the new photo (id = "3") is inserted
        coVerify(exactly = 1) {
            dao.insertAll(match { entities ->
                entities.size == 1 && entities[0].id == "3"
            })
        }
    }

}
