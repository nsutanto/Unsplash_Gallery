package com.nsutanto.photoviews

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.nsutanto.photoviews.model.Photo
import com.nsutanto.photoviews.model.PhotoUrls
import com.nsutanto.photoviews.model.PhotoUser
import com.nsutanto.photoviews.repository.IPhotoRepository
import com.nsutanto.photoviews.viewmodel.PhotoGalleryViewModel
import com.nsutanto.photoviews.viewmodel.SharedPhotoState
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoGalleryViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

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
    fun `onPhotoClicked should update SharedPhotoState with correct photoId`() = runTest {
        val pagingData = PagingData.from(testPhotos)
        val flow = flowOf(pagingData)
        every { repository.photoPager } returns flow
        mockkObject(SharedPhotoState)
        viewModel = PhotoGalleryViewModel(repository)
        advanceUntilIdle()

        viewModel.onPhotoClicked("1")
        advanceUntilIdle()

        coVerify(exactly = 1) {
            SharedPhotoState.updateCurrentPhotoId("1")
        }
        unmockkObject(SharedPhotoState)
    }
}


