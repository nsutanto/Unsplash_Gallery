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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.nsutanto.photoviews.R
import com.nsutanto.photoviews.model.Photo
import com.nsutanto.photoviews.util.SharePhotoLink
import com.nsutanto.photoviews.viewmodel.PhotoDetailViewModel
import com.nsutanto.photoviews.viewmodel.SharedPhotoState
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

@Composable
fun PhotoDetail(
    viewModel: PhotoDetailViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val photoId by SharedPhotoState.currentPhotoId.collectAsState()
    val initialIndex by viewModel.initialIndex.collectAsState()

    val photos = viewModel.photoPagingFlow.collectAsLazyPagingItems()

    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = {
            photos.itemCount
        }
    )

    // Scroll to the photo with matching ID once photos are loaded
    LaunchedEffect(photos.itemSnapshotList.items, photoId) {
        val index = photos.itemSnapshotList.items.indexOfFirst { it.id == photoId }
        if (index >= 0) {
            pagerState.scrollToPage(index)
            viewModel.setCurrentPhotoId(photoId)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { index ->
        val photo = photos[index]
        if (photo != null) {
            PhotoDetailContent(
                photo = photo,
                onShare = { SharePhotoLink.shareImageUrl(context, photo.urls?.regular ?: "") }
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }


    // Update shared state as user swipes
    //LaunchedEffect(pagerState) {
    //    snapshotFlow { pagerState.currentPage }
    //        .distinctUntilChanged()
    //        .collect { index ->
    //            viewModel.setCurrentPhotoId(photos[index]?.id)
    //        }
    //}


}

@Composable
fun PhotoDetailContent(photo: Photo, onShare: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        photo.urls?.regular?.let { url ->
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
            photo.user?.username?.let { username ->
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






