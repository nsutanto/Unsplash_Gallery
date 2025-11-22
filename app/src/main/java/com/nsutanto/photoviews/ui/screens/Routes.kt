package com.nsutanto.photoviews.ui.screens

sealed class Screen(val route: String) {
    object MainScreen: Screen("main_screen")
    object SecondScreen: Screen("second_screen")
}