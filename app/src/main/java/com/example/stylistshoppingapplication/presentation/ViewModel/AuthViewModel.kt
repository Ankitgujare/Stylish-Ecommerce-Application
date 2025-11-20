package com.example.stylistshoppingapplication.presentation.ViewModel

import android.content.Context
import androidx.compose.runtime.Recomposer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylistshoppingapplication.data.local.model.ProfileEntity
import com.example.stylistshoppingapplication.data.local.repository.ProfileRepository
import com.example.stylistshoppingapplication.domain.usecases.SetUserPrefrenceUseCase
import com.example.stylistshoppingapplication.domain.usecases.SignupUsecase
import com.example.stylistshoppingapplication.domain.usecases.GoogleSignInUseCase
import com.example.stylistshoppingapplication.domain.util.Results
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthCredential
import com.store.ecommerceapplication.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val signupUsecase: SignupUsecase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val setUserPreferencesUseCase: SetUserPrefrenceUseCase,
    private val context: Context
):ViewModel() {

    private val _authState = MutableStateFlow<Results<String>>(Results.Ideal)
    val authState = _authState.asStateFlow()

    fun login(email: String, password: String) {
        _authState.value = Results.Loading
        viewModelScope.launch {
            try {
                val result = loginUseCase(email, password)
                _authState.value = result
                if (result is Results.Success) {
                    // Mark as logged in and not first time anymore
                     setUserPreferencesUseCase.SetLogeIn(true)
                    setUserPreferencesUseCase.setFirstTimeLogedIn(false)
                }
            } catch (e: Exception) {
                _authState.value = Results.Failure(e.message ?: "Login failed")
            }
        }
    }


    fun signup(email: String, password: String) {
        _authState.value = Results.Loading
        viewModelScope.launch {
            try {
                val result = signupUsecase(email, password)
                _authState.value = result
                if (result is Results.Success) {
                    // After signup, user is logged in but it's their first time
                    setUserPreferencesUseCase.SetLogeIn(true)
                    setUserPreferencesUseCase.setFirstTimeLogedIn(true) // First time user
                }
            } catch (e: Exception) {
                _authState.value = Results.Failure(e.message ?: "Signup failed")
            }
        }
    }

    fun signInWithGoogle(credential: AuthCredential) {
        _authState.value = Results.Loading
        viewModelScope.launch {
            try {
                val result = googleSignInUseCase(credential)
                _authState.value = result
                if (result is Results.Success) {
                    // Check if user already exists in our system
                    val auth = FirebaseAuth.getInstance()
                    val user = auth.currentUser
                    
                    if (user != null) {
                        // For Google Sign-In, we need to determine if this is a new user
                        // We'll check if the user has a display name set (indicating they've completed onboarding)
                        val isFirstTime = user.displayName.isNullOrEmpty()
                        
                        // Mark as logged in
                        setUserPreferencesUseCase.SetLogeIn(true)
                        setUserPreferencesUseCase.setFirstTimeLogedIn(isFirstTime)
                        
                        // Save Google profile data to local database
                        saveGoogleProfileToDatabase(user)
                    } else {
                        // Fallback: treat as new user
                        setUserPreferencesUseCase.SetLogeIn(true)
                        setUserPreferencesUseCase.setFirstTimeLogedIn(true)
                    }
                }
            } catch (e: Exception) {
                _authState.value = Results.Failure(e.message ?: "Sign-In failed")
            }
        }
    }
    
    private fun saveGoogleProfileToDatabase(user: com.google.firebase.auth.FirebaseUser) {
        viewModelScope.launch {
            try {
                val profileRepository = ProfileRepository(context)
                val profileEntity = ProfileEntity(
                    id = user.uid,
                    name = user.displayName ?: "",
                    email = user.email ?: "",
                    photoUrl = user.photoUrl?.toString(),
                    pincode = "",
                    address = "",
                    city = "",
                    state = "",
                    country = "",
                    bankAccount = "",
                    accountHolder = "",
                    ifscCode = ""
                )
                profileRepository.insertProfile(profileEntity)
            } catch (e: Exception) {
                // Log error but don't fail the sign-in process
                e.printStackTrace()
            }
        }
    }
    
    fun signInWithCredential(credential: AuthCredential) {
        _authState.value = Results.Loading
        viewModelScope.launch {
            try {
                val auth = FirebaseAuth.getInstance()
                val result = auth.signInWithCredential(credential).await()
                _authState.value = Results.Success("Phone authentication successful")
                // Mark as logged in and not first time anymore
                setUserPreferencesUseCase.SetLogeIn(true)
                setUserPreferencesUseCase.setFirstTimeLogedIn(false)
            } catch (e: Exception) {
                _authState.value = Results.Failure(e.message ?: "Phone authentication failed")
            }
        }
    }
}