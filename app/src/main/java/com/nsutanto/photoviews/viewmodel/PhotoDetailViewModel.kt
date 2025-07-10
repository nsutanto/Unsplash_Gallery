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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PhotoDetailViewModel(private val repository: IPhotoRepository) : ViewModel() {

    private val _initialIndex = MutableStateFlow(0)
    val initialIndex: StateFlow<Int> = _initialIndex

    // Paging flow for the UI to collect as LazyPagingItems
    val photoPagingFlow: Flow<PagingData<Photo>> = repository
        .getPhotoPager()
        .cachedIn(viewModelScope)

    init {
       


        viewModelScope.launch {
            SharedPhotoState.currentPhotoId.collectLatest { currentId ->
                if (currentId != null) {
                    _initialIndex.value = 0 // default in case it's not found
                }
            }
        }
    }

    fun setCurrentPhotoId(photoId: String?) {
        SharedPhotoState.updateCurrentPhotoId(photoId)
    }
}


    /*

    data class PhotoDetail(
        val id: String? = null,
        val url: String? = null,
        val userName: String? = null,
        val description: String? = null
    )

    private val _currentPhoto = MutableStateFlow<PhotoDetail?>(null)
    val currentPhoto: StateFlow<PhotoDetail?> = _currentPhoto

    private val _photoListSize = MutableStateFlow(0)
    val photoListSize: StateFlow<Int> = _photoListSize

    private val _initialIndex = MutableStateFlow(0)
    val initialIndex: StateFlow<Int> = _initialIndex

    //private var _photoList = listOf<Photo>()
    private val pagingItems = mutableListOf<Photo>()

    private val cachedPhotos = repository.getPhotoPager()
        .cachedIn(viewModelScope) // 🔒 private internal flow


    init {
        val cachedPhotos = repository.getPhotoPager()
            .cachedIn(viewModelScope)

        // Collect PagingData once and keep snapshot list updated
        viewModelScope.launch {
            cachedPhotos.collectLatest { pagingData ->
                pagingData.asSnapshot()
               // pagingData.collect { photo ->
               //     pagingItems.add(photo)
                    _photoListSize.value = pagingItems.size
                //}
            }
        }

        // Then react to currentPhotoId changes
        viewModelScope.launch {
            SharedPhotoState.currentPhotoId
                .collectLatest { photoId ->
                    val index = pagingItems.indexOfFirst { it.id == photoId }
                    if (index >= 0) {
                        _initialIndex.value = index
                        _currentPhoto.value = pagingItems[index].toPhotoDetail()
                    }
                }
        }
    }

    fun setCurrentPhotoIdByIndex(photoIndex: Int) {
        // Update the current photo id in the shared state so that it can scroll to the correct photo
        //val photoId = _photoList.getOrNull(photoIndex)?.id
        //SharedPhotoState.updateCurrentPhotoId(photoId)
    }

    private fun Photo.toPhotoDetail() = PhotoDetail(
        id = id,
        url = urls?.regular,
        userName = user?.username,
        description = description
    )

     */


//}