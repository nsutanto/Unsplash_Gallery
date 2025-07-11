package com.nsutanto.photoviews.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nsutanto.photoviews.viewmodel.PhotoViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val viewModel: PhotoViewModel = koinViewModel()
    NavHost(navController, startDestination = Screen.PhotoGallery.route, modifier = modifier) {
        composable(Screen.PhotoGallery.route) {
            PhotoGallery(viewModel = viewModel,
                onPhotoClick = {
                    navController.navigate(Screen.PhotoDetail.route)
                }
            )
        }
        composable(route = Screen.PhotoDetail.route) {
            PhotoDetail(viewModel = viewModel)
        }
    }
}