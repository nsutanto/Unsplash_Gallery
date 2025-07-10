package com.nsutanto.photoviews.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nsutanto.photoviews.model.Photo
import com.nsutanto.photoviews.repository.IPhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhotoGalleryViewModel(private val repository: IPhotoRepository) : ViewModel() {

    val photoPagingFlow: Flow<PagingData<Photo>> = repository
        .getPhotoPager()
        .cachedIn(viewModelScope)

    private val _currentPhotoId = MutableStateFlow<String?>(null)
    val currentPhotoId: StateFlow<String?> = _currentPhotoId

    init {
        // Collect last viewed photo index
        viewModelScope.launch {
            SharedPhotoState.currentPhotoId.collect { photoId ->
                _currentPhotoId.value = photoId
            }
        }
    }

    fun onPhotoClicked(id: String?) {
        // Get photo id by index
        viewModelScope.launch {
            SharedPhotoState.updateCurrentPhotoId(id)
        }
    }
}