package com.example.stylistshoppingapplication.domain.usecases

import com.example.stylistshoppingapplication.domain.reposatory.userPreferenceReposatory.UserPreferenceReposatory

class SetUserPreferenceUseCase(
    private val userPreferenceRepo:UserPreferenceReposatory) {


     suspend fun setFirstTimeLoggedIn(isFirstTime:Boolean){
         userPreferenceRepo.IsFirstTimeLogin(isFirstTime)
     }

     suspend fun setLoggedIn(isLoggedIn:Boolean){
        userPreferenceRepo.IsLogedIn(isLoggedIn)
     }

     suspend fun setDarkMode(isDarkMode:Boolean){
         userPreferenceRepo.setDarkMode(isDarkMode)
     }



}