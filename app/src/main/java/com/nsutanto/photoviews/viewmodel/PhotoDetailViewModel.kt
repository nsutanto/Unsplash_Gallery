package com.nsutanto.photoviews.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nsutanto.photoviews.repository.IPhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhotoDetailViewModel(private val repository: IPhotoRepository) : ViewModel() {

    data class PhotoDetail(
        val id: String? = null,
        val url: String? = null,
        val userName: String? = null,
        val description: String? = null
    )

    private val _photos = MutableStateFlow<List<PhotoDetail>>(emptyList())
    val photos: StateFlow<List<PhotoDetail>> = _photos

    init {
        viewModelScope.launch {
            repository.photoFlow.collect { photos ->

                val photoDetails = photos.map { photo ->
                    PhotoDetail(
                        id = photo.id,
                        url = photo.urls?.regular,
                        userName = photo.user?.username,
                        description = photo.description
                    )
                }
                _photos.value = photoDetails
            }
        }
    }

    fun getPhotoIndexById(photoId: String): Int {
        val photoIndex = _photos.value.indexOfFirst { it.id == photoId }.coerceAtLeast(0)
        setCurrentPhotoIdByIndex(photoIndex)
        return photoIndex
    }

    fun setCurrentPhotoIdByIndex(photoIndex: Int) {
        val photoId = _photos.value.getOrNull(photoIndex)?.id
        SharedPhotoState.updateCurrentPhotoId(photoId)
    }
}