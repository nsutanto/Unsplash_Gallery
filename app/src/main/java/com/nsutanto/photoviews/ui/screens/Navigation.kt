package com.nsutanto.photoviews.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nsutanto.photoviews.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = koinViewModel()
    NavHost(navController, startDestination = Screen.MainScreen.route, modifier = modifier) {
        composable(Screen.MainScreen.route) {
            MainScreen(
                viewModel = mainViewModel,
                onButtonClicked = {
                    navController.navigate(Screen.SecondScreen.route)
                }
            )
        }
        composable(Screen.SecondScreen.route) {
            SecondScreen(
                viewModel = mainViewModel
            )
        }
    }
}