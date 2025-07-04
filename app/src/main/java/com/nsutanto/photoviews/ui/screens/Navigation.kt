package com.nsutanto.photoviews.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.PhotoGallery.route) {
        composable(Screen.PhotoGallery.route) {
            PhotoGallery(
                onPhotoClick = { photoId ->
                    navController.navigate(Screen.PhotoDetail.createRoute(photoId))
                }
            )
        }
        composable(
            route = Screen.PhotoDetail.route,
            arguments = listOf(navArgument("photoId") {
                type = NavType.StringType }
            )
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getString("photoId")
            photoId?.let {
                PhotoDetail(photoId = it)
            }
        }
    }
}