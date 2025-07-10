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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.nsutanto.photoviews.viewmodel.PhotoGalleryViewModel
import coil.compose.AsyncImage
import com.nsutanto.photoviews.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun PhotoGallery(viewModel: PhotoGalleryViewModel = koinViewModel(),
                 onPhotoClick: () -> Unit) {
    // Observe the Paging data for photos
    val photos = viewModel.photoPagingFlow.collectAsLazyPagingItems()

    // Collect the current photo index and API status
    val currentPhotoId by viewModel.currentPhotoId.collectAsStateWithLifecycle()

    // Create grid state and context for scrolling and showing error
    val gridState = rememberLazyGridState()
    val context = LocalContext.current

    // Scroll to the current photo when index changes

    LaunchedEffect(currentPhotoId) {
        currentPhotoId?.let {
            val index = photos.itemSnapshotList.items.indexOfFirst { it.id == currentPhotoId }
            if (index >= 0) {
                gridState.scrollToItem(index)
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        // LazyVerticalGrid for infinite scroll
        LazyVerticalGrid(
            columns = GridCells.Fixed(1), // Adjust grid to 2 columns (you can tweak this for responsiveness)
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(dimensionResource(id = R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            // Use itemsIndexed to access items safely in the Paging data
            items(photos.itemCount) { index ->
                val photo = photos[index]
                photo?.urls?.regular?.let { url ->
                    PhotoItem(
                        url = url,
                        onClick = {
                            viewModel.onPhotoClicked(photos[index]?.id)
                            onPhotoClick()
                        }
                    )
                }
            }
        }
        photos.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                loadState.refresh is LoadState.Error -> {
                    Toast.makeText(context, stringResource(id = R.string.error_message), Toast.LENGTH_SHORT).show()
                }
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
        placeholder = painterResource(R.drawable.image_placeholder),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable { onClick() }
    )
}
