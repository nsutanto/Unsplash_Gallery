package com.nsutanto.photoviews.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nsutanto.photoviews.model.Photo
import com.nsutanto.photoviews.model.PhotoUrls
import com.nsutanto.photoviews.model.PhotoUser

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey val id: String,
    val url: String?,
    val username: String?,
    val description: String?
)

fun Photo.toEntity(): PhotoEntity = PhotoEntity(
    id = this.id,
    url = this.urls?.regular,
    username = this.user?.username,
    description = this.description
)

fun PhotoEntity.toPhoto(): Photo = Photo(
    id = id,
    urls = PhotoUrls(regular = url),
    user = PhotoUser(username = username),
    description = description
)