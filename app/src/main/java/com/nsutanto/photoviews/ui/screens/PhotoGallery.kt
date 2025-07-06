package com.nsutanto.photoviews.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nsutanto.photoviews.viewmodel.PhotoGalleryViewModel
import coil.compose.AsyncImage
import com.nsutanto.photoviews.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.koinViewModel

@Composable
fun PhotoGallery(viewModel: PhotoGalleryViewModel = koinViewModel(),
                 onPhotoClick: () -> Unit) {
    val photoUrls by viewModel.photoListUrl.collectAsStateWithLifecycle()
    val currentPhotoIndex by viewModel.lastViewedIndex.collectAsStateWithLifecycle()
    val apiStatus by viewModel.apiStatus.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()
    val context = LocalContext.current

    if (apiStatus == PhotoGalleryViewModel.APIStatus.ERROR) {
        Toast.makeText(context, "Error fetching getting photos", Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(currentPhotoIndex) {
        currentPhotoIndex?.let { index ->
            gridState.scrollToItem(index = index, scrollOffset = 0)
        }
    }

    // Trigger fetchPhotos when reaching near the end
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo }
            .map { layoutInfo ->
                // counting last visible, needs to use first() since last() will return big number during initialization.
                // Otherwise it will try to re-compose and re-fetch multiple times
                layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
            }
            .distinctUntilChanged()
            .collectLatest { lastVisible ->
                viewModel.fetchNextPageIfNeeded(lastVisible)
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(dimensionResource(id = R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            items(photoUrls.size) { index ->
                PhotoItem(
                    url = photoUrls[index],
                    onClick = {
                        viewModel.onPhotoClicked(index)
                        onPhotoClick()
                    }
                )
            }
        }

        // Loading indicator
        if (apiStatus == PhotoGalleryViewModel.APIStatus.LOADING) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
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
        placeholder = painterResource(R.drawable.image_placeholder),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable { onClick() }
    )
}
