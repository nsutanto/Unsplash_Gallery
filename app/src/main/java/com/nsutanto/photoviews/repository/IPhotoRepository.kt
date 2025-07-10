package com.nsutanto.photoviews.repository

import androidx.paging.PagingData
import com.nsutanto.photoviews.model.Photo
import kotlinx.coroutines.flow.Flow

interface IPhotoRepository {
    val photoPager: Flow<PagingData<Photo>>
}