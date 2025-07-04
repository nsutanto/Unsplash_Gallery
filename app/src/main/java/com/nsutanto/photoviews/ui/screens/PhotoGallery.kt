package com.nsutanto.photoviews.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nsutanto.photoviews.viewmodel.PhotoGalleryViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.koinViewModel

@Composable
fun PhotoGallery(viewModel: PhotoGalleryViewModel = koinViewModel(),
                 onPhotoClick: (String) -> Unit) {
    val photoUrls by viewModel.photoListUrl.collectAsStateWithLifecycle()
    val currentPhotoIndex by viewModel.lastViewedIndex.collectAsStateWithLifecycle()
    val apiStatus by viewModel.apiStatus.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    // Observe if photo is clicked so that we can navigate to the detail screen
    LaunchedEffect(Unit) {
        viewModel.selectedPhotoId.collect { photoId ->
            photoId?.let {
                onPhotoClick(photoId)
                viewModel.onNavigationHandled()
            }
        }
    }

    // Get the last viewed photo index and scroll to it
    LaunchedEffect(currentPhotoIndex) {
        currentPhotoIndex?.let {
            listState.animateScrollToItem(it)
        }
    }

    // Implement the infinite scroll feature
    // TODO: Fix this logic to avoid fetching too many times
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .map { layoutInfo ->
                val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItems = layoutInfo.totalItemsCount
                lastVisible to totalItems
            }
            .distinctUntilChanged()
            .collect { (lastVisible, totalItems) ->
                if (lastVisible >= totalItems - 3) {
                    println("***** Photo Gallery: Fetching more photos...")
                    viewModel.fetchPhotos()
                }
            }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(photoUrls.size) { index ->
                PhotoItem(
                    url = photoUrls[index],
                    onClick = {
                        viewModel.onPhotoClicked(index)
                    }
                )
            }
        }
        if (apiStatus == PhotoGalleryViewModel.APIStatus.LOADING) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Composable
fun PhotoItem(url: String, onClick: () -> Unit) {
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray)
            .clickable { onClick() }
    )
}
