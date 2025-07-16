package com.nsutanto.photoviews.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.nsutanto.photoviews.api.IApiService
import com.nsutanto.photoviews.db.AppDatabase
import com.nsutanto.photoviews.db.PhotoDao
import com.nsutanto.photoviews.db.PhotoEntity
import com.nsutanto.photoviews.db.toEntity

// https://developer.android.com/topic/libraries/architecture/paging/v3-network-db
@OptIn(ExperimentalPagingApi::class)
class PhotoRemoteMediator(
    private val api: IApiService,
    private val dao: PhotoDao,
    private val db: AppDatabase
) : RemoteMediator<Int, PhotoEntity>() {

    private var currentPage = 1

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PhotoEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> { 1 }
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    currentPage
                }
            }

            val photos = api.fetchPhotos(page)
            currentPage++

            db.withTransaction {
                //if (loadType == LoadType.REFRESH) {
                //    dao.clearAll()
                //}
                dao.insertAll(photos.map { it.toEntity() })
            }

            MediatorResult.Success(endOfPaginationReached = photos.isEmpty())

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}

