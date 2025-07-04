package com.nsutanto.photoviews.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nsutanto.photoviews.viewmodel.PhotoDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PhotoDetail(viewModel: PhotoDetailViewModel = koinViewModel(),
                photoId: String) {

    val photoList by viewModel.photos.collectAsStateWithLifecycle()

    if (photoList.isEmpty()) {
        // You can show a loading indicator or blank screen
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Find the initial page based on photoId
    val initialPage = remember(photoId to photoList) {
        viewModel.getPhotoIndexById(photoId)
    }

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { photoList.size }
    )

    // Handle back press and return current photo id
    //BackHandler {
    //    val currentPhotoId = photoList.getOrNull(pagerState.currentPage)?.id
    //    if (currentPhotoId != null) {
    //        //onBackWithPhotoId(currentPhotoId)
    //    }
    //}

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val photo = photoList[page]
        AsyncImage(
            model = photo.urls?.regular,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }

}
/*
    // Display the photo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        photoDetail.url?.let { photoUrl ->
            AsyncImage(
                model = photoUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        } ?: run {
            // To do: handle error
        }
    }

 */




