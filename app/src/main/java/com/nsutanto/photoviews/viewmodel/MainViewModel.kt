package com.nsutanto.photoviews.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nsutanto.photoviews.model.MainResponse
import com.nsutanto.photoviews.repository.IMainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(val repository: IMainRepository): ViewModel() {

    private var _viewModelData = MutableStateFlow<MainResponse?>(null)
    val viewModelData: StateFlow<MainResponse?> = _viewModelData

    init {
        viewModelScope.launch {
            repository.apiResponse.collect {
                _viewModelData.value = it
            }
        }
        viewModelScope.launch {
            repository.fetchApi()
        }
    }
}