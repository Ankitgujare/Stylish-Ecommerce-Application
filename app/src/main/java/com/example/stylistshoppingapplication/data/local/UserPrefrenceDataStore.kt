package com.example.stylistshoppingapplication.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPrefrenceDataStore(private val context:Context) {


    companion object{
        private val Context.datastore:DataStore<Preferences>by preferencesDataStore("user-preferences")
        private val IS_FIRST_TIME_LOGIN= booleanPreferencesKey("is_first_time_login")
        private val IS_LOGGED_IN= booleanPreferencesKey("is_logged_in")
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }


    val isFirsttimeLogin: Flow<Boolean> = context.datastore.data.map { preferences->
        preferences[IS_FIRST_TIME_LOGIN]?:true
    }

    val isLogin: Flow<Boolean> = context.datastore.data.map { preferences->
        preferences[IS_LOGGED_IN]?:false
    }

    val isDarkMode: Flow<Boolean> = context.datastore.data.map { preferences ->
        preferences[IS_DARK_MODE] ?: false
    }


   suspend fun setFirstTimeLogin(firstTimeLogin:Boolean){
        context.datastore.edit {preferences->
            preferences[IS_FIRST_TIME_LOGIN]=firstTimeLogin
        }

    }

    suspend fun isLogedIn(isLogedIn:Boolean){
        context.datastore.edit { preferences->
            preferences[IS_LOGGED_IN]=isLogedIn
        }
    }

    suspend fun setDarkMode(isDarkMode: Boolean) {
        context.datastore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDarkMode
        }
    }
}