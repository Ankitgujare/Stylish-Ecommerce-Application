package com.example.stylistshoppingapplication.presentation.ViewModel.viewModelFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stylistshoppingapplication.data.local.UserPrefrenceDataStore
import com.example.stylistshoppingapplication.data.reposatoryImp.UserPrefrenceImplementation
import com.example.stylistshoppingapplication.domain.usecases.GetUserPreferencesUseCase
import com.example.stylistshoppingapplication.domain.usecases.SetUserPreferenceUseCase
import com.example.stylistshoppingapplication.presentation.ViewModel.userPrefrenceViewModel.UserPrefrenceViewModel

class UserPrefrenceViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserPrefrenceViewModel::class.java)) {
            val dataStore = UserPrefrenceDataStore(context)
            val repository = UserPrefrenceImplementation(dataStore)
            val getUserPreferencesUseCase = GetUserPreferencesUseCase(repository)
            val setUserPreferenceUseCase = SetUserPreferenceUseCase(repository)
            
            @Suppress("UNCHECKED_CAST")
            return UserPrefrenceViewModel(getUserPreferencesUseCase, setUserPreferenceUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
