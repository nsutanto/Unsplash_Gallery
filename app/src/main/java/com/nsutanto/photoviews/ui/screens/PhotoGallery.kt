package com.nsutanto.photoviews.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nsutanto.photoviews.viewmodel.PhotoGalleryViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.koinViewModel

@Composable
fun PhotoGallery(viewModel: PhotoGalleryViewModel = koinViewModel(),
                 onPhotoClick: (String) -> Unit) {
    val photoUrls by viewModel.photoListUrl.collectAsStateWithLifecycle()
    val currentPhotoIndex by viewModel.lastViewedIndex.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()

    // Observe if photo is clicked so that we can navigate to the detail screen
    LaunchedEffect(Unit) {
        viewModel.selectedPhotoId.collect { photoId ->
            photoId?.let {
                onPhotoClick(photoId)
                viewModel.onNavigationHandled()
            }
        }
    }

    LaunchedEffect(currentPhotoIndex) {
        currentPhotoIndex?.let { index ->
            println("***** Scrolling to index: $index")
            gridState.scrollToItem(index = index, scrollOffset = 0)
        }
    }

    // Trigger fetchPhotos when reaching near the end
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo }
            .map { layoutInfo ->
                // counting last visible, needs to use first() since last() will return big number during initialization.
                // Otherwise it will try to re-compose and re-fetch multiple times
                val lastVisible = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
                val totalItems = layoutInfo.totalItemsCount
                lastVisible to totalItems
            }
            .distinctUntilChanged()
            .collectLatest { (lastVisible, totalItems) ->
                val isNearBottom = lastVisible >= totalItems - 3 // start fetching when 3 items are left

                if (isNearBottom) {
                    viewModel.fetchPhotos()
                }
            }
    }


    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        state = gridState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
