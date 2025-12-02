package com.example.stylistshoppingapplication

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels






import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


import com.example.stylistshoppingapplication.navigation.AppNavigation
import com.example.stylistshoppingapplication.presentation.ViewModel.AuthViewModel

import com.example.stylistshoppingapplication.presentation.ViewModel.viewModelFactory.AuthViewModelFactory
import com.example.stylistshoppingapplication.ui.theme.StylistShoppingApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            throwable.printStackTrace()
            // You can also log to a file here if needed
        }

        super.onCreate(savedInstanceState)
        setContent {
            val context: Context = LocalContext.current
            // Create UserPrefrenceViewModel to observe theme
            val userPrefFactory = com.example.stylistshoppingapplication.presentation.ViewModel.viewModelFactory.UserPrefrenceViewModelFactory(context)
            val userPrefViewModel: com.example.stylistshoppingapplication.presentation.ViewModel.userPrefrenceViewModel.UserPrefrenceViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = userPrefFactory)
            val userPrefState by userPrefViewModel.state.collectAsState()

            StylistShoppingApplicationTheme(
                darkTheme = userPrefState.isDarkMode
            ) {
                
                //Create the ViewModelFactory instance
                val factory = AuthViewModelFactory(context)
                // ✅ Pass the factory into viewModels()
                val authViewModel: AuthViewModel by viewModels { factory }
                AppNavigation(authViewModel,context)

            }
        }
    }
}


