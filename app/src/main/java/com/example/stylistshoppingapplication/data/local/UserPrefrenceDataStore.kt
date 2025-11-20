package com.example.stylistshoppingapplication.data.local

import android.content.Context
import androidx.compose.ui.platform.LocalContext

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
        private val Context.datastore:DataStore<Preferences>by preferencesDataStore("user-prefrences")
        private val IS_FIRST_TIME_LOGIN= booleanPreferencesKey("is_first_time_login")
        private val IS_LOGGED_IN= booleanPreferencesKey("Is_loged_In")
    }


    val isFirsttimeLogin: Flow<Boolean> = context.datastore.data.map { preferences->
        preferences[IS_FIRST_TIME_LOGIN]?:true
    }

    val isLogin: Flow<Boolean> = context.datastore.data.map { preferences->
        preferences[IS_LOGGED_IN]?:false
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




}