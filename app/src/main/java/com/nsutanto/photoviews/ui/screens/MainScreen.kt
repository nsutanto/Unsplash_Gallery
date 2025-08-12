package com.nsutanto.photoviews.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nsutanto.photoviews.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = koinViewModel()) {
    // UI content goes here
    val helloWorldText = viewModel.helloWorld.collectAsStateWithLifecycle()

    Text(text = helloWorldText.value)
}