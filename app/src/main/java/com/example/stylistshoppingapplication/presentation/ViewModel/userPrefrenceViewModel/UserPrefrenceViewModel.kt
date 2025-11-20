package com.example.stylistshoppingapplication.presentation.ViewModel.userPrefrenceViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylistshoppingapplication.domain.model.UserPrefrenceState
import com.example.stylistshoppingapplication.domain.usecases.GetUserPreferencesUseCase
import com.example.stylistshoppingapplication.domain.usecases.SetUserPrefrenceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class UserPrefrenceViewModel(
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val setUserPreferencesUseCase: SetUserPrefrenceUseCase
):ViewModel() {

    private val _state= MutableStateFlow(UserPrefrenceState())
    val state:StateFlow<UserPrefrenceState> = _state.asStateFlow();


    init {
        observeUsePrefrences()
    }

   private fun observeUsePrefrences(){
        viewModelScope.launch {
           combine(
               getUserPreferencesUseCase.isFirstTimeLogin(),
               getUserPreferencesUseCase.isLogedIn()
           ){isFirstTime,IsLogedIn->
               UserPrefrenceState(
                    isFirstTimeLogedIn=isFirstTime,
                    isLogedIn=IsLogedIn,
                    isLoading=false
               )

           }.collect{newstate->
               _state.value=newstate
           }
        }
    }


    fun setFirstTimeLogin(isFirstTime:Boolean){
        viewModelScope.launch {
            setUserPreferencesUseCase.setFirstTimeLogedIn(isFirstTime)
        }
    }


    fun setLogedIn(isLogedIn:Boolean){
        viewModelScope.launch {
            setUserPreferencesUseCase.SetLogeIn(isLogedIn)
        }
    }









}