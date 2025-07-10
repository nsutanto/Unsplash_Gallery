package com.nsutanto.photoviews.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nsutanto.photoviews.model.Photo
import com.nsutanto.photoviews.repository.IPhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PhotoGalleryViewModel(private val repository: IPhotoRepository) : ViewModel() {

    val photoPagingFlow: Flow<PagingData<Photo>> = repository
        .getPhotoPager()
        .cachedIn(viewModelScope)

    private val _lastViewedIndex = MutableStateFlow<Int?>(null)
    val lastViewedIndex: StateFlow<Int?> = _lastViewedIndex

    private var _photoList = listOf<Photo>()

    init {
        // Collect photos from repository
        /*
        viewModelScope.launch {
            repository.photoFlow.collectLatest { photos ->
                _photoList = photos
                _photoListUrl.value = photos.mapNotNull { it.urls?.regular }
            }
        }

         */

        // Collect last viewed photo index
        viewModelScope.launch {
            SharedPhotoState.currentPhotoId.collect { photoId ->
                photoId?.let { id ->
                    val photoIndex = _photoList.indexOfFirst { it.id == id }
                    _lastViewedIndex.value = photoIndex
                }
            }
        }
    }

    fun onPhotoClicked(id: String?) {
        // Get photo id by index
        viewModelScope.launch {
            //val id = _photoList[index].id
            // Update the current photo id in the shared state so the photo detail screen can open the right photo
            println("***** On Photo Clicked: $id *****")
            SharedPhotoState.updateCurrentPhotoId(id)
        }
    }
}