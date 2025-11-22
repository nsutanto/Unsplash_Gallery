package com.nsutanto.photoviews.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nsutanto.photoviews.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = koinViewModel(),
               onButtonClicked: () -> Unit) {

    val viewModelData by viewModel.viewModelData.collectAsStateWithLifecycle()

    viewModelData?.products?.let { products ->
        LazyColumn {
            // Add 5 items
            items(products) { product ->
                ProductItem(product.title ?: "")
            }
        }
    }
}

@Composable
fun ProductItem(title: String) {
    Text(text = title)
}