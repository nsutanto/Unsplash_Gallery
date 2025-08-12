package com.nsutanto.photoviews.repository

import kotlinx.coroutines.flow.Flow

interface IMainRepository {
    // Define repository methods here

    val helloWorld: Flow<String>

    suspend fun getHelloWorld()
}