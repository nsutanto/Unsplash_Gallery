package com.nsutanto.photoviews.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.PhotoGallery.route) {
        composable(Screen.PhotoGallery.route) {
            PhotoGallery(
                onPhotoClick = {
                    navController.navigate(Screen.PhotoDetail.route)
                }
            )
        }
        composable(route = Screen.PhotoDetail.route) {
            PhotoDetail()
        }
    }
}