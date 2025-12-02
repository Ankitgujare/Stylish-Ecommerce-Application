package com.example.stylistshoppingapplication.presentation.ViewModel.viewModelFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stylistshoppingapplication.presentation.ViewModel.ProfileViewModel

class ProfileViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
