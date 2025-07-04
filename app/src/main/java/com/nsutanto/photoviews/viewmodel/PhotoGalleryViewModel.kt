package com.nsutanto.photoviews.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nsutanto.photoviews.api.ApiService
import com.nsutanto.photoviews.model.Photo
import com.nsutanto.photoviews.repository.IPhotoRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class PhotoGalleryViewModel(private val repository: IPhotoRepository) : ViewModel() {

    enum class APIStatus { INIT, ERROR, LOADING }
    private var currentPage = 1

    private val _photoListUrl = MutableStateFlow<List<String>>(emptyList())
    val photoListUrl: StateFlow<List<String>> = _photoListUrl

    private val _selectedPhotoId = MutableSharedFlow<String?>()
    val selectedPhotoId = _selectedPhotoId.asSharedFlow()

    private val _lastViewedIndex = MutableStateFlow<Int?>(null)
    val lastViewedIndex: StateFlow<Int?> = _lastViewedIndex

    private val _apiStatus = MutableStateFlow(APIStatus.INIT)
    val apiStatus: StateFlow<APIStatus> = _apiStatus

    private var photoList = mutableListOf<Photo>()

    init {
        // Collect photos from repository
        viewModelScope.launch {
            repository.photoFlow.collect { photos ->
                println("***** Photo Size is ${photos.size}")
                photoList = photos.toMutableList()
                _photoListUrl.value = photos.mapNotNull { it.urls?.regular }

                // Handle initialization. If we have photos already, we want to set the current page correctly based on number of photos per page
                currentPage = if (photos.isEmpty()) {
                    1
                } else {
                    photos.size / ApiService.PER_PAGE
                }
            }
        }

        // Collect last viewed photo index
        viewModelScope.launch {
            SharedPhotoState.currentPhotoId.collect { photoId ->
                photoId?.let { id ->
                    val photoIndex = photoList.indexOfFirst { it.id == id }
                    _lastViewedIndex.value = photoIndex
                }
            }
        }

        // Fetch photos
        fetchPhotos()
    }

    fun fetchPhotos() {
        if (_apiStatus.value == APIStatus.LOADING) {
            return
        }
        viewModelScope.launch {
            _apiStatus.value = APIStatus.LOADING
            try {
                repository.fetchPhotos(currentPage)
                currentPage++
                _apiStatus.value = APIStatus.INIT
            } catch (e: Exception) {
                println("***** Fetch Photos Exception: ${e.message}")
                // TODO: Implement proper error handling on the UI
                _apiStatus.value = APIStatus.ERROR
            }
        }
    }

    fun onPhotoClicked(index: Int) {
        // Get photo id by index
        viewModelScope.launch {
            val id = photoList[index].id
            _selectedPhotoId.emit(id)
            SharedPhotoState.updateCurrentPhotoId(id)
        }
    }

    fun onNavigationHandled() {
        // Reset so that the next click will trigger the navigation again
        viewModelScope.launch {
            _selectedPhotoId.emit(null)
        }
    }
}