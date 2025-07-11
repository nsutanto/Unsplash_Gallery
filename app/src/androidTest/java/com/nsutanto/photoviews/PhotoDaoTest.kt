@file:OptIn(androidx.paging.ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
package com.nsutanto.photoviews

import com.nsutanto.photoviews.db.PhotoEntity
import com.nsutanto.photoviews.db.AppDatabase
import com.nsutanto.photoviews.db.PhotoDao
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PhotoDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: PhotoDao

    private val testPhotos = listOf(
        PhotoEntity("1", "url1", "user1", "desc1"),
        PhotoEntity("2", "url2", "user2", "desc2"),
        PhotoEntity("3", "url3", "user3", "desc3")
    )

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = db.photoDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insert_and_query_photos() = runTest {
        dao.insertAll(testPhotos)

        val pagingSource = dao.pagingSource()
        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )
        val data = (loadResult as PagingSource.LoadResult.Page).data
        Assert.assertEquals(3, data.size)
        Assert.assertEquals("1", data[0].id)
        Assert.assertEquals("2", data[1].id)
        Assert.assertEquals("3", data[2].id)
    }

    @Test
    fun clearAll_should_remove_all_photos() = runTest {
        dao.insertAll(testPhotos)
        dao.clearAll()

        val pagingSource = dao.pagingSource()
        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )
        val data = (loadResult as PagingSource.LoadResult.Page).data
        Assert.assertTrue(data.isEmpty())
    }
}
