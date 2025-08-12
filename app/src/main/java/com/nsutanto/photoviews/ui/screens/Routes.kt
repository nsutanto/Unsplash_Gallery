package com.nsutanto.photoviews.ui.screens

sealed class Screen(val route: String) {

    object MainScreen: Screen("main_screen")
    object PhotoGallery : Screen("photo_gallery")
    object PhotoDetail : Screen("photo_detail")
}