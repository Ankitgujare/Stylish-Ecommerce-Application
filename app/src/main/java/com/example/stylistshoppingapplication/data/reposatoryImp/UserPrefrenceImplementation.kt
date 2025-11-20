package com.example.stylistshoppingapplication.data.reposatoryImp

import android.preference.PreferenceDataStore
import com.example.stylistshoppingapplication.data.local.UserPrefrenceDataStore
import com.example.stylistshoppingapplication.domain.reposatory.userPreferenceReposatory.UserPreferenceReposatory
import kotlinx.coroutines.flow.Flow

class UserPrefrenceImplementation(
    private val userPreferenceDataStore: UserPrefrenceDataStore,
) :UserPreferenceReposatory {


    override val isFirstTimeLogin: Flow<Boolean> = userPreferenceDataStore.isFirsttimeLogin

    override val isLogedIn:Flow<Boolean> = userPreferenceDataStore.isLogin


    override suspend fun IsFirstTimeLogin(isfirstTimeLogin: Boolean) {
        userPreferenceDataStore.setFirstTimeLogin(isfirstTimeLogin)
    }

    override suspend fun IsLogedIn(isLogedIn: Boolean) {
        userPreferenceDataStore.isLogedIn(isLogedIn)
    }
}