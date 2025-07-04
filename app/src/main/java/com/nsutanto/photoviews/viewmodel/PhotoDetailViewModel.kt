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

    init {
        viewModelScope.launch {
            repository.photoFlow.collect { photos ->
                photoList = photos.toMutableList()
            }
        }
    }

    fun getPhotoDetail(photoId: String) {
        val url = photoList.find { it.id == photoId }?.urls?.regular
        _photoDetailState.value = PhotoDetail(url = url)
    }
}