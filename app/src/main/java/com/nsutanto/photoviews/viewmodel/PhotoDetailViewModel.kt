package com.nsutanto.photoviews.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nsutanto.photoviews.model.Photo
import com.nsutanto.photoviews.repository.IPhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhotoDetailViewModel(private val repository: IPhotoRepository) : ViewModel() {

    data class PhotoDetail(
        val url: String? = null,
        val userName: String? = null,
    )

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos

    private val _photoUrls = MutableStateFlow<List<String>>(emptyList())
    val photoUrls: StateFlow<List<String>> = _photoUrls

    init {
        viewModelScope.launch {
            repository.photoFlow.collect { photoList ->
                _photos.value = photoList
            }
        }
    }

    fun getPhotoIndexById(photoId: String): Int {
        return _photos.value.indexOfFirst { it.id == photoId }.coerceAtLeast(0)
    }

    /*data class PhotoDetail(
        val url: String? = null,
        val userName: String? = null,
    )



    private val _photoDetailState = MutableStateFlow(PhotoDetail())
    val photoDetailState: StateFlow<PhotoDetail> = _photoDetailState

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private var photoList = mutableListOf<Photo>()

    init {
        viewModelScope.launch {
            repository.photoFlow.collect { photos ->
                photoList = photos.toMutableList()
            }
        }
    }

    fun getPhotoDetail(photoId: String) {
        _currentIndex.value = photoList.indexOfFirst { it.id == photoId }.coerceAtLeast(0)
        updatePhotoDetail()
    }

    fun getPhotoUrls(): List<String> {
        return photoList.mapNotNull { it.urls?.regular }
    }

    private fun updatePhotoDetail() {
        val photo = photoList.getOrNull(_currentIndex.value)
        _photoDetailState.value = PhotoDetail(
            url = photo?.urls?.regular,
        )
    }*/


}