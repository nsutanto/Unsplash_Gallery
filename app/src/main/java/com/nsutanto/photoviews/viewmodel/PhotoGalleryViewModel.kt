package com.nsutanto.photoviews.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nsutanto.photoviews.model.Photo
import com.nsutanto.photoviews.repository.IPhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PhotoGalleryViewModel(private val repository: IPhotoRepository) : ViewModel() {

    enum class APIStatus { INIT, ERROR, LOADING }

    private var currentPage = 1

    private val _photoListUrl = MutableStateFlow<List<String>>(emptyList())
    val photoListUrl: StateFlow<List<String>> = _photoListUrl

    private val _apiStatus = MutableStateFlow(APIStatus.INIT)
    val apiStatus: StateFlow<APIStatus> = _apiStatus

    private val _lastViewedIndex = MutableStateFlow<Int?>(null)
    val lastViewedIndex: StateFlow<Int?> = _lastViewedIndex

    private var _photoList = listOf<Photo>()

    init {
        // Collect photos from repository
        viewModelScope.launch {
            repository.photoFlow.collectLatest { photos ->
                _photoList = photos
                _photoListUrl.value = photos.mapNotNull { it.urls?.regular }
            }
        }

        // Collect last viewed photo index
        viewModelScope.launch {
            SharedPhotoState.currentPhotoId.collect { photoId ->
                photoId?.let { id ->
                    val photoIndex = _photoList.indexOfFirst { it.id == id }
                    _lastViewedIndex.value = photoIndex
                }
            }
        }

        // Fetch photos
        fetchPhotos()
    }

    fun fetchPhotos() {
        viewModelScope.launch {
            _apiStatus.value = APIStatus.LOADING
            try {
                repository.fetchPhotos(currentPage)
                currentPage++
                _apiStatus.value = APIStatus.INIT
            } catch (e: Exception) {
                _apiStatus.value = APIStatus.ERROR
            }
        }
    }

    fun onPhotoClicked(index: Int) {
        // Get photo id by index
        viewModelScope.launch {
            val id = _photoList[index].id
            // Update the current photo id in the shared state so the photo detail screen can open the right photo
            SharedPhotoState.updateCurrentPhotoId(id)
        }
    }

    fun fetchNextPageIfNeeded(lastVisibleIndex: Int) {
        val isNearBottom = lastVisibleIndex >= _photoList.size - LAST_ITEM_TO_FETCH

        if (isNearBottom) {
            fetchPhotos()
        }
    }

    companion object {
        const val LAST_ITEM_TO_FETCH = 5 // constant to determine how many items left before fetching more
    }
}