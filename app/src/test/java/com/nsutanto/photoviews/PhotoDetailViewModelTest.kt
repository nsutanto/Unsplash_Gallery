package com.nsutanto.photoviews

import com.nsutanto.photoviews.model.Photo
import com.nsutanto.photoviews.model.PhotoUrls
import com.nsutanto.photoviews.model.PhotoUser
import com.nsutanto.photoviews.repository.IPhotoRepository
import com.nsutanto.photoviews.viewmodel.PhotoDetailViewModel
import com.nsutanto.photoviews.viewmodel.SharedPhotoState
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository = mockk<IPhotoRepository>(relaxed = true)

    private val testPhotos = listOf(
        Photo(id = "1", urls = PhotoUrls("url1"), user = PhotoUser("user1"), description = "desc1"),
        Photo(id = "2", urls = PhotoUrls("url2"), user = PhotoUser("user2"), description = "desc2"),
        Photo(id = "3", urls = PhotoUrls("url3"), user = PhotoUser("user3"), description = "desc3")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should map photoFlow into photoDetails`() = runTest {
        val flow = MutableStateFlow(testPhotos)
        every { repository.photoFlow } returns flow

        val viewModel = PhotoDetailViewModel(repository)
        advanceUntilIdle()


        val details = viewModel.photos.value
        assertEquals(3, details.size)
        assertEquals("1", details[0].id)
        assertEquals("user2", details[1].userName)
        assertEquals("desc3", details[2].description)
    }

    @Test
    fun `should update initialIndex when currentPhotoId matches`() = runTest {

        val flow = MutableStateFlow(testPhotos)
        every { repository.photoFlow } returns flow

        val viewModel = PhotoDetailViewModel(repository)
        advanceUntilIdle()


        SharedPhotoState.updateCurrentPhotoId("2")
        advanceUntilIdle()

        // Index is 1 for photoId 2
        assertEquals(1, viewModel.initialIndex.value)
    }

    @Test
    fun `setCurrentPhotoIdByIndex should update SharedPhotoState with correct photoId`() = runTest {
        val flow = MutableStateFlow(testPhotos)
        every { repository.photoFlow } returns flow
        mockkObject(SharedPhotoState)
        val viewModel = PhotoDetailViewModel(repository)
        advanceUntilIdle()

        viewModel.setCurrentPhotoIdByIndex(1)
        advanceUntilIdle()

        coVerify(exactly = 1) {
            SharedPhotoState.updateCurrentPhotoId("2")
        }
        unmockkObject(SharedPhotoState)
    }
}
