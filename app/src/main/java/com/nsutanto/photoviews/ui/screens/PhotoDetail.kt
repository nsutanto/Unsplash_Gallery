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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.koinViewModel

@Composable
fun PhotoDetail(
    viewModel: PhotoDetailViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val photoId by viewModel.currentPhotoId.collectAsStateWithLifecycle()
    val photoDetailState by viewModel.photoDetailState.collectAsStateWithLifecycle()
    val photos = photoDetailState.currentPhotoFlow.collectAsLazyPagingItems()

    val isLoaded = photos.loadState.refresh is LoadState.NotLoading

    // Remember the index only once it is valid
    val targetIndex = remember(photoId, photos.itemSnapshotList.items) {
        photos.itemSnapshotList.indexOfFirst { it?.id == photoId }.takeIf { it >= 0 }
    }

    // Only create pagerState after the index is available
    //val pagerState = targetIndex?.let {
    //    rememberPagerState(initialPage = it, pageCount = { photos.itemCount })
    //}

    val pagerState = targetIndex?.let { rememberPagerState(initialPage = it, pageCount = { photos.itemCount }) }



    if (!isLoaded || pagerState == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            if (pageIndex < photos.itemCount) {
                val photo = photos[pageIndex]
                println("***** HorizontalPager index: $pageIndex, ID: ${photo?.id}")
                if (photo != null) {
                    PhotoDetailContent(
                        photo = photo,
                        onShare = {
                            photo.url?.let { SharePhotoLink.shareImageUrl(context, it) }
                        }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        // Update current photo ID when user swipes
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }
                .distinctUntilChanged()
                .collect { index ->
                    val photo = photos.itemSnapshotList.getOrNull(index)
                    viewModel.setCurrentPhotoId(photo?.id)
                }
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
