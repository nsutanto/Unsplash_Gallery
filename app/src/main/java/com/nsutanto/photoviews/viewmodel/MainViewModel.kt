package com.nsutanto.photoviews.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nsutanto.photoviews.repository.IMainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: IMainRepository) : ViewModel() {

    private val _helloWorld = MutableStateFlow("")
    val helloWorld : StateFlow<String> = _helloWorld

    init {
        viewModelScope.launch {
            repository.helloWorld.collect {
                _helloWorld.value = it
            }
        }
        viewModelScope.launch {
            repository.getHelloWorld()
        }
    }
}