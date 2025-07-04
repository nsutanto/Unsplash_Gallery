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
    // TODO: Refactor this, might just use the Photo Detail object instead of photo list url
    private val _photoListUrl = MutableStateFlow<List<String>>(emptyList())
    val photoListUrl: StateFlow<List<String>> = _photoListUrl

    private var photoList = mutableListOf<Photo>()

    init {
        viewModelScope.launch {
            repository.photoFlow.collect { photos ->
                photoList = photos.toMutableList()
                _photoListUrl.value = photos.mapNotNull { it.urls?.regular }
            }
        }
    }

    fun getPhotoIndexById(photoId: String): Int {
        val photoIndex = photoList.indexOfFirst { it.id == photoId }.coerceAtLeast(0)
        setCurrentPhoto(photoIndex)
        return photoIndex
    }

    fun setCurrentPhoto(photoIndex: Int) {
        val photoId = photoList.getOrNull(photoIndex)?.id
        SharedPhotoState.updateCurrentPhotoId(photoId)
    }
}