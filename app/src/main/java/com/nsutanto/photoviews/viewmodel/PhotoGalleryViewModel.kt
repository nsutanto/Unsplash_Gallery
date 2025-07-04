package com.nsutanto.photoviews.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nsutanto.photoviews.repository.PhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhotoGalleryViewModel : ViewModel() {
    private val repository = PhotoRepository()

    private val _photoListUrl = MutableStateFlow<List<String>>(emptyList())
    val photoListUrl: StateFlow<List<String>> = _photoListUrl

    init {
        viewModelScope.launch {
            repository.photoFlow.collect { photos ->
                _photoListUrl.value = photos.mapNotNull { it.urls?.regular }
            }
        }
        fetchPhotos()
    }

    private fun fetchPhotos() {
        viewModelScope.launch {
            repository.fetchPhotos()
        }
    }
}