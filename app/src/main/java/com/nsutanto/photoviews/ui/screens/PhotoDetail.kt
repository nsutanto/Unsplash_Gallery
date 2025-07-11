package com.nsutanto.photoviews.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.nsutanto.photoviews.R
import com.nsutanto.photoviews.util.SharePhotoLink
import com.nsutanto.photoviews.viewmodel.PhotoDetailViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

@Composable
fun PhotoDetail(
    viewModel: PhotoDetailViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val photoId by viewModel.currentPhotoId.collectAsStateWithLifecycle()
    val photoDetailState by viewModel.photoDetailState.collectAsStateWithLifecycle()

    val photos = photoDetailState.currentPhotoFlow.collectAsLazyPagingItems()

    // Show a loading spinner until we have photos and have scrolled
    val isLoading = photos.loadState.refresh is LoadState.Loading
    val index = photos.itemSnapshotList.indexOfFirst { it?.id == photoId }

    // State to track if initial scroll is done
    val hasScrolled = remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { photos.itemCount }
    )

    // Try to scroll to the photo once the photoId is available and photos are loaded
    LaunchedEffect(photoId, photos.itemSnapshotList.items) {
        if (!hasScrolled.value && photoId != null) {
            if (index >= 0) {
                pagerState.scrollToPage(index)
                hasScrolled.value = true
            }
        }
    }



    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (index == -1) {
        // Data loaded, but correct photo not found yet
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // index >= 0, show pager and scroll to correct photo
        LaunchedEffect(index) {
            if (!hasScrolled.value) {
                pagerState.scrollToPage(index)
                hasScrolled.value = true
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val photo = photos[pageIndex]
            if (photo != null) {
                PhotoDetailContent(
                    photo = photo,
                    onShare = {
                        photo.url?.let { SharePhotoLink.shareImageUrl(context, it) }
                    }
                )
            }
        }
    }

    // Update current photo ID when swiping
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { index ->
                val photo = photos.itemSnapshotList.getOrNull(index)
                viewModel.setCurrentPhotoId(photo?.id)
            }
    }
}

@Composable
fun PhotoDetailContent(photo: PhotoDetailViewModel.PhotoDetail, onShare: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        photo.url?.let { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            photo.userName?.let { username ->
                Text(
                    text = stringResource(id = R.string.photo_detail_username, username),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            photo.description?.let { desc ->
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Button(
                onClick = onShare,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(id = R.dimen.padding_small))
            ) {
                Text(stringResource(id = R.string.share_photo_link))
            }
        }
    }
}
