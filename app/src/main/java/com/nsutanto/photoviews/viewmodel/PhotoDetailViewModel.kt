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

    private val _photoDetailState = MutableStateFlow(PhotoDetail())
    val photoDetailState: StateFlow<PhotoDetail> = _photoDetailState

    private var photoList = mutableListOf<Photo>()
    private var currentIndex = 0

    init {
        viewModelScope.launch {
            repository.photoFlow.collect { photos ->
                photoList = photos.toMutableList()
            }
        }
    }

    fun getPhotoDetail(photoId: String) {
        currentIndex = photoList.indexOfFirst { it.id == photoId }.coerceAtLeast(0)
        updatePhotoDetail()
    }

    fun getPhotoUrls(): List<String> {
        return photoList.mapNotNull { it.urls?.regular }
    }

    fun swipeLeft() {
        if (currentIndex > 0) {
            currentIndex--
            updatePhotoDetail()
        }
    }

    fun swipeRight() {
        if (currentIndex < photoList.lastIndex) {
            currentIndex++
            updatePhotoDetail()
        }
    }

    private fun updatePhotoDetail() {
        val photo = photoList.getOrNull(currentIndex)
        _photoDetailState.value = PhotoDetail(
            url = photo?.urls?.regular,
        )
    }
}