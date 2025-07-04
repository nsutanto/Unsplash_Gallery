package com.nsutanto.photoviews.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nsutanto.photoviews.viewmodel.PhotoDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PhotoDetail(viewModel: PhotoDetailViewModel = koinViewModel(),
                photoId: String) {

    val photoUrls by viewModel.photoListUrl.collectAsStateWithLifecycle()

    if (photoUrls.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Find the initial page based on photoId
    val initialPage = remember(photoId to photoUrls) {
        viewModel.getPhotoIndexById(photoId)
    }

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = {
            photoUrls.size
        }
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
        AsyncImage(
            model = photoUrls[page],
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}




