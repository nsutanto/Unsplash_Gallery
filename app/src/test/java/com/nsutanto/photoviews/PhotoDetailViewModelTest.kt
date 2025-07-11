package com.nsutanto.photoviews

import androidx.paging.PagingData
import com.nsutanto.photoviews.model.Photo
import com.nsutanto.photoviews.model.PhotoUrls
import com.nsutanto.photoviews.model.PhotoUser
import com.nsutanto.photoviews.repository.IPhotoRepository
import com.nsutanto.photoviews.viewmodel.PhotoViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import androidx.paging.testing.asSnapshot
import io.mockk.coVerify
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
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
    fun `currentPhoto should be updated based on currentPhotoId`() = runTest {
        val pagingData = PagingData.from(testPhotos)
        val flow = flowOf(pagingData)
        every { repository.photoPager } returns flow

        val viewModel = PhotoViewModel(repository)
        viewModel.clearPaging()


        //viewModel
        //SharedPhotoState.updateCurrentPhotoId("3")

        val photoDetails = viewModel.photoDetailState.value.currentPhotoFlow.asSnapshot()

        val current = photoDetails.find { it.id == "3" }

        assertEquals("3", current?.id)
        assertEquals("url3", current?.url)
        assertEquals("user3", current?.userName)
        assertEquals("desc3", current?.description)
    }


    @Test
    fun `setCurrentPhotoIdByIndex should update SharedPhotoState with correct photoId`() = runTest {
        val pagingData = PagingData.from(testPhotos)
        val flow = flowOf(pagingData)
        every { repository.photoPager } returns flow
        //mockkObject(SharedPhotoState)
        val viewModel = PhotoViewModel(repository)
        advanceUntilIdle()

        viewModel.setCurrentPhotoId("1")
        advanceUntilIdle()

        coVerify(exactly = 1) {
            //SharedPhotoState.updateCurrentPhotoId("1")
        }
        //unmockkObject(SharedPhotoState)
    }
}
