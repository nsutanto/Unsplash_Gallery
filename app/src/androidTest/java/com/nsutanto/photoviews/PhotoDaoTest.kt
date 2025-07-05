package com.nsutanto.photoviews

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nsutanto.photoviews.db.AppDatabase
import com.nsutanto.photoviews.db.PhotoDao
import com.nsutanto.photoviews.db.PhotoEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhotoDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var photoDao: PhotoDao

    @Before
    fun setup() {
        // Use in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries() // only for testing
            .build()

        photoDao = database.photoDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetPhotos_shouldReturnInsertedData() = runTest {
        val photoEntities = listOf(
            PhotoEntity(id = "1", url = "url1", username = "user1", description = "desc1"),
            PhotoEntity(id = "2", url = "url2", username = "user2", description = "desc2")
        )

        photoDao.insertAll(photoEntities)
        val result = photoDao.getAll()

        assertEquals(2, result.size)
        assertEquals("url1", result[0].url)
        assertEquals("user2", result[1].username)
    }

    @Test
    fun insertAll_shouldReplaceOnConflict() = runTest {
        val photo1 = PhotoEntity(id = "1", url = "url1", username = "user1", description = "desc1")
        val photo1Updated = PhotoEntity(id = "1", url = "url1Updated", username = "user1", description = "desc1")

        photoDao.insertAll(listOf(photo1))
        photoDao.insertAll(listOf(photo1Updated))

        val result = photoDao.getAll()

        assertEquals(1, result.size)
        assertEquals("url1Updated", result[0].url)
    }
}