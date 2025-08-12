package com.nsutanto.photoviews.repository

import com.nsutanto.photoviews.api.IApiService
import kotlinx.coroutines.flow.MutableStateFlow

class MainRepository(private val api: IApiService) : IMainRepository {
    // Implement repository methods here

    private val _helloWorld = MutableStateFlow("")
    override val helloWorld = _helloWorld

    override suspend fun getHelloWorld() {
        _helloWorld.value = "Hello World"
    }
}