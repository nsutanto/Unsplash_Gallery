package com.nsutanto.photoviews.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nsutanto.photoviews.repository.PhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhotoGalleryViewModel : ViewModel() {
    private val repository = PhotoRepository()

    enum class APIStatus { INIT, ERROR, LOADING }
    private var currentPage = 1

    private val _photoListUrl = MutableStateFlow<List<String>>(emptyList())
    val photoListUrl: StateFlow<List<String>> = _photoListUrl

    private val _apiStatus = MutableStateFlow(APIStatus.INIT)
    val apiStatus: StateFlow<APIStatus> = _apiStatus

    init {
        viewModelScope.launch {
            repository.photoFlow.collect { photos ->
                _photoListUrl.value = photos.mapNotNull { it.urls?.regular }
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
}