package com.nsutanto.photoviews.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.nsutanto.photoviews.model.Photo
import com.nsutanto.photoviews.repository.IPhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class PhotoViewModel(private val repository: IPhotoRepository) : ViewModel() {

    data class PhotoDetail(
        val id: String? = null,
        val url: String? = null,
        val userName: String? = null,
        val description: String? = null
    )

    data class PhotoDetailState(
        val currentPhotoFlow: Flow<PagingData<PhotoDetail>>
    )

    private val _photoDetailState = MutableStateFlow(PhotoDetailState(getPhotoDetailFlow().cachedIn(viewModelScope)))
    val photoDetailState: StateFlow<PhotoDetailState> = _photoDetailState

    private val _currentPhotoId = MutableStateFlow<String?>(null)
    val currentPhotoId: StateFlow<String?> = _currentPhotoId


    fun setCurrentPhotoId(photoId: String?) {
        photoId?.let {
            if (_currentPhotoId.value != photoId) {
                _currentPhotoId.value = photoId
            }
        }
    }

    fun clearPaging() {
        _photoDetailState.update { PhotoDetailState(getPhotoDetailFlow()) }
    }

    private fun getPhotoDetailFlow(): Flow<PagingData<PhotoDetail>> {
        return repository.photoPager.map { pagingData ->
                pagingData.map { it.toPhotoDetail() }
        }
    }

    private fun Photo.toPhotoDetail() = PhotoDetail(
        id = id,
        url = urls?.regular,
        userName = user?.username,
        description = description
    )
}