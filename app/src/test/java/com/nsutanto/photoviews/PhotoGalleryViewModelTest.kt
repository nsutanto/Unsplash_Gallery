package com.nsutanto.photoviews

import app.cash.turbine.test
import com.nsutanto.photoviews.model.Photo
import com.nsutanto.photoviews.model.PhotoUrls
import com.nsutanto.photoviews.model.PhotoUser
import com.nsutanto.photoviews.repository.IPhotoRepository
import com.nsutanto.photoviews.viewmodel.PhotoGalleryViewModel
import com.nsutanto.photoviews.viewmodel.SharedPhotoState
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class PhotoGalleryViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val repository = mockk<IPhotoRepository>()
    private lateinit var viewModel: PhotoGalleryViewModel

    private val testPhotos = listOf(
        Photo(id = "1", urls = PhotoUrls("url1"), user = PhotoUser("user1"), description = "desc1"),
        Photo(id = "2", urls = PhotoUrls("url2"), user = PhotoUser("user2"), description = "desc2")
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        SharedPhotoState.updateCurrentPhotoId(null)
    }

    @After
    fun tearDown() {
        // Reset any shared state or singletons here
        SharedPhotoState.updateCurrentPhotoId(null)
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load photos and update url list`() = runTest {
        val flow = MutableStateFlow(testPhotos)
        coEvery { repository.photoFlow } returns flow
        coEvery { repository.fetchPhotos(any()) } just Runs

        viewModel = PhotoGalleryViewModel(repository)
        advanceUntilIdle()
        viewModel.photoListUrl.test {
            val result = awaitItem()
            assertEquals(listOf("url1", "url2"), result)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchPhotos should emit ERROR status when API fails`() = runTest {
        val photoFlow = MutableStateFlow<List<Photo>>(emptyList())
        coEvery { repository.photoFlow } returns photoFlow
        coEvery { repository.fetchPhotos(any()) } throws Exception("API failed")

        val viewModel = PhotoGalleryViewModel(repository)

        viewModel.apiStatus.test {
            viewModel.fetchPhotos()

            val emissions = mutableListOf<PhotoGalleryViewModel.APIStatus>()
            repeat(3) { // It emit init / loading, we just care about error
                emissions.add(awaitItem())
            }

            assertTrue(emissions.contains(PhotoGalleryViewModel.APIStatus.ERROR))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchPhotos should request incremented page number on each call`() = runTest {
        val capturedPages = mutableListOf<Int>()
        every { repository.photoFlow } returns MutableStateFlow(emptyList())
        coEvery { repository.fetchPhotos(capture(capturedPages)) } just Runs

        viewModel = PhotoGalleryViewModel(repository)

        advanceUntilIdle() // for init
        viewModel.fetchPhotos()
        advanceUntilIdle()
        viewModel.fetchPhotos()
        advanceUntilIdle()

        // Assert that the pages were 1 (Init), 2, 3
        assertEquals(listOf(1, 2, 3), capturedPages)
    }



    @Test
    fun `onPhotoClicked should update SharedPhotoState`() = runTest {
        val flow = MutableStateFlow(testPhotos)
        every { repository.photoFlow } returns flow
        coEvery { repository.fetchPhotos(any()) } just Runs

        mockkObject(SharedPhotoState)
        coEvery { SharedPhotoState.updateCurrentPhotoId(any()) } just Runs

        viewModel = PhotoGalleryViewModel(repository)

        // Wait for photoList to be updated
        viewModel.photoListUrl.first { it.isNotEmpty() }

        viewModel.onPhotoClicked(1)
        advanceUntilIdle()

        coVerify(exactly = 1) {
            SharedPhotoState.updateCurrentPhotoId("2")
        }
        unmockkObject(SharedPhotoState)
    }

}
