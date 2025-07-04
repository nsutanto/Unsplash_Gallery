package com.nsutanto.photoviews.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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


    val photoDetail by viewModel.photoDetailState.collectAsStateWithLifecycle()

    /// Initialize pager state
    //val pagerState = rememberPagerState(page)

    // Update ViewModel when the current page changes in the pager
    //LaunchedEffect(pagerState.currentPage) {
      //  viewModel.getPhotoDetail(photoDetailState.value.id ?: "")
    //}

    // Update the view model with the initial photo ID
    LaunchedEffect(photoId) {
        viewModel.getPhotoDetail(photoId)
    }

    // Create a list of photo URLs
    /*
    val photoUrls = remember { viewModel.getPhotoUrls() }


    // Display the pager
    HorizontalPager(
        pageCount = photoUrls.size,
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        // Get the image URL from the list
        val url = photoUrls[page]
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }

    // Handle the back button press
    BackHandler {
        //onBackPressed(photoDetailState.id ?: "")
    }

     */

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


}

