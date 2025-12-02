package com.example.stylistshoppingapplication.presentation.ViewModel.viewModelFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stylistshoppingapplication.data.local.UserPrefrenceDataStore
import com.example.stylistshoppingapplication.data.local.repository.ProfileRepository
import com.example.stylistshoppingapplication.data.reposatoryImp.AuthReposatoryImp
import com.example.stylistshoppingapplication.data.reposatoryImp.UserPrefrenceImplementation
import com.example.stylistshoppingapplication.domain.usecases.SetUserPreferenceUseCase
import com.example.stylistshoppingapplication.domain.usecases.SignupUsecase
import com.example.stylistshoppingapplication.domain.usecases.GoogleSignInUseCase
import com.example.stylistshoppingapplication.presentation.ViewModel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.store.ecommerceapplication.domain.usecase.LoginUseCase


class AuthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val firebaseAuth = FirebaseAuth.getInstance()
            val repository = AuthReposatoryImp(firebaseAuth)
            val loginUseCase = LoginUseCase(repository)
            val signupUsecase=SignupUsecase(repository)
            val googleSignInUseCase = GoogleSignInUseCase(repository)

            // Build UserPreference-related dependencies using context
            val userPreferencesDataStore = UserPrefrenceDataStore(context)
            val userPreferenceRepository = UserPrefrenceImplementation(userPreferencesDataStore)
            val setUserPreferenceUseCase = SetUserPreferenceUseCase(userPreferenceRepository)


            val profileRepository = ProfileRepository(context)
            return AuthViewModel(loginUseCase, signupUsecase, googleSignInUseCase, setUserPreferenceUseCase, profileRepository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}