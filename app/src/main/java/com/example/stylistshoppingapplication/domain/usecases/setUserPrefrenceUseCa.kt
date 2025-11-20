package com.example.stylistshoppingapplication.domain.usecases

import com.example.stylistshoppingapplication.domain.reposatory.userPreferenceReposatory.UserPreferenceReposatory

class SetUserPrefrenceUseCase(
    private val UserPrefrenceRepo:UserPreferenceReposatory) {


     suspend fun setFirstTimeLogedIn(isFirtTime:Boolean){
         UserPrefrenceRepo.IsFirstTimeLogin(isFirtTime)
     }

     suspend fun SetLogeIn(isLogedIn:Boolean){
        UserPrefrenceRepo.IsLogedIn(isLogedIn)
     }



}