package com.example.stylistshoppingapplication.domain.reposatory.userPreferenceReposatory

import kotlinx.coroutines.flow.Flow

interface UserPreferenceReposatory {

    val isFirstTimeLogin:Flow<Boolean>
    val isLogedIn: Flow<Boolean>
    suspend fun IsFirstTimeLogin(isfirstTimeLogin:Boolean)
    suspend fun IsLogedIn(isLogedIn:Boolean)

}