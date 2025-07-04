package com.nsutanto.photoviews.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SharedPhotoState {
    private val _currentPhotoId = MutableStateFlow<String?>(null)
    val currentPhotoId: StateFlow<String?> = _currentPhotoId

    fun updateCurrentPhotoId(photoId: String?) {
        _currentPhotoId.value = photoId
    }
}