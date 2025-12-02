package com.example.stylistshoppingapplication.presentation.ViewModel.viewModelFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stylistshoppingapplication.presentation.ViewModel.CartViewModel

class CartViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}