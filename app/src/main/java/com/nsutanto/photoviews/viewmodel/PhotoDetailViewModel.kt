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
        val id: String? = null,
        val url: String? = null,
        val userName: String? = null,
        val description: String? = null
    )
    // TODO: Refactor this, might just use the Photo Detail object instead of photo list url
    //private val _photoListUrl = MutableStateFlow<List<String>>(emptyList())
    //val photoListUrl: StateFlow<List<String>> = _photoListUrl

    private val _photos = MutableStateFlow<List<PhotoDetail>>(emptyList())
    val photos: StateFlow<List<PhotoDetail>> = _photos

    //private var photoList = mutableListOf<Photo>()

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

                //photoList = photos.toMutableList()
                //_photoListUrl.value = photos.mapNotNull { it.urls?.regular }
            }
        }
    }

    fun getPhotoIndexById(photoId: String): Int {
        val photoIndex = _photos.value.indexOfFirst { it.id == photoId }.coerceAtLeast(0)
        setCurrentPhoto(photoIndex)
        return photoIndex
    }

    fun setCurrentPhoto(photoIndex: Int) {
        val photoId = _photos.value.getOrNull(photoIndex)?.id
        SharedPhotoState.updateCurrentPhotoId(photoId)
    }
}