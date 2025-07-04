package com.nsutanto.photoviews.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        viewModelScope.launch {
            repository.photoFlow.collect { photos ->
                photoList = photos.toMutableList()
                _photoListUrl.value = photos.mapNotNull { it.urls?.regular }
            }
        }

        viewModelScope.launch {
            SharedPhotoState.currentPhotoId.collect { photoId ->
                photoId?.let { id ->
                    val photoIndex = photoList.indexOfFirst { it.id == id }
                    _lastViewedIndex.value = photoIndex
                }
            }
        }


        fetchPhotos()
    }

    fun fetchPhotos() {
        if (_apiStatus.value == APIStatus.LOADING) {
            return
        }
        _apiStatus.value = APIStatus.LOADING

        viewModelScope.launch {
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
        viewModelScope.launch {
            photoList[index].id?.let { id ->
                _selectedPhotoId.emit(id)
            }
        }
    }

    fun onNavigationHandled() {
        viewModelScope.launch {
            _selectedPhotoId.emit(null)
        }
    }
}