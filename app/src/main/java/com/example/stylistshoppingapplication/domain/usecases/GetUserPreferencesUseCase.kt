package com.example.stylistshoppingapplication.domain.usecases

import com.example.stylistshoppingapplication.domain.reposatory.userPreferenceReposatory.UserPreferenceReposatory
import kotlinx.coroutines.flow.Flow

class GetUserPreferencesUseCase(
    private val userPreferenceReposatory: UserPreferenceReposatory
) {


    fun isFirstTimeLogin():Flow<Boolean> = userPreferenceReposatory.isFirstTimeLogin
    fun isLogedIn():Flow<Boolean> = userPreferenceReposatory.isLogedIn
    fun isDarkMode():Flow<Boolean> = userPreferenceReposatory.isDarkMode



}