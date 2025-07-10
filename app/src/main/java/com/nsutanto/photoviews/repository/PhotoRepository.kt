package com.nsutanto.photoviews.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.nsutanto.photoviews.db.PhotoDao
import com.nsutanto.photoviews.db.toPhoto
import com.nsutanto.photoviews.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOn

@OptIn(ExperimentalPagingApi::class)
class PhotoRepository(
    private val dao: PhotoDao,
    private val remoteMediator: PhotoRemoteMediator
) : IPhotoRepository {

    override fun getPhotoPager(): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = remoteMediator,
            pagingSourceFactory = { dao.pagingSource() }
        ).flow
            .map { pagingData -> pagingData.map { it.toPhoto() } }
            .flowOn(Dispatchers.IO)
    }
}
