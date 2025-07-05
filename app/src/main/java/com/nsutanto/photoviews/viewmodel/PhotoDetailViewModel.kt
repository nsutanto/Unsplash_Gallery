package com.nsutanto.photoviews.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nsutanto.photoviews.repository.IPhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
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

    private val _initialIndex = MutableStateFlow(0)
    val initialIndex: StateFlow<Int> = _initialIndex

    init {
        viewModelScope.launch {
            combine(
                repository.photoFlow,
                SharedPhotoState.currentPhotoId
            ) { photos, currentPhotoId ->
                val photoDetails = photos.map { photo ->
                    PhotoDetail(
                        id = photo.id,
                        url = photo.urls?.regular,
                        userName = photo.user?.username,
                        description = photo.description
                    )
                }
                val initialIndex = currentPhotoId?.let { id ->
                    photoDetails.indexOfFirst { it.id == id }.takeIf { it >= 0 }
                } ?: 0

                Pair(photoDetails, initialIndex)

            }.collectLatest { (photoDetails, initialIndex) ->
                _photos.value = photoDetails
                _initialIndex.value = initialIndex

            }
        }
    }

    fun setCurrentPhotoIdByIndex(photoIndex: Int) {
        // Update the current photo id in the shared state so that it can scroll to the correct photo
        val photoId = _photos.value.getOrNull(photoIndex)?.id
        SharedPhotoState.updateCurrentPhotoId(photoId)
    }
}