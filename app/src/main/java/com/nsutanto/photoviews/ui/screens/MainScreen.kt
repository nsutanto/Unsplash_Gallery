package com.nsutanto.photoviews.ui.screens

import androidx.compose.runtime.Composable
import com.nsutanto.photoviews.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = koinViewModel(),
               onButtonClicked: () -> Unit) {

}