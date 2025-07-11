package com.nsutanto.photoviews.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.nsutanto.photoviews.api.ApiService.Companion.PER_PAGE
import com.nsutanto.photoviews.db.PhotoDao
import com.nsutanto.photoviews.db.toPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOn

@OptIn(ExperimentalPagingApi::class)
class PhotoRepository(
    private val dao: PhotoDao,
    private val remoteMediator: PhotoRemoteMediator
) : IPhotoRepository {

    override val photoPager = Pager(
            config = PagingConfig(pageSize = PER_PAGE, prefetchDistance = PREFETCH_DISTANCE),
                remoteMediator = remoteMediator,
            pagingSourceFactory = { dao.pagingSource() }
        ).flow
            .map { pagingData -> pagingData.map { it.toPhoto() } }
            .flowOn(Dispatchers.IO)


    companion object {
        const val PAGE_SIZE = 30
        const val PREFETCH_DISTANCE = PAGE_SIZE * 5
    }

}
