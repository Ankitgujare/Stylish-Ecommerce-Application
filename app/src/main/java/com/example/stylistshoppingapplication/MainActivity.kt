package com.example.stylistshoppingapplication

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stylistshoppingapplication.navigation.AppNavigation
import com.example.stylistshoppingapplication.presentation.ViewModel.AuthViewModel
import com.example.stylistshoppingapplication.presentation.ViewModel.ProductViewModel
import com.example.stylistshoppingapplication.presentation.ViewModel.viewModelFactory.AuthViewModelFactory
import com.example.stylistshoppingapplication.ui.theme.StylistShoppingApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            StylistShoppingApplicationTheme {
                val context: Context = LocalContext.current
                //Create the ViewModelFactory instance
                val factory = AuthViewModelFactory(context)
                // ✅ Pass the factory into viewModels()
                val authViewModel: AuthViewModel by viewModels { factory }
                AppNavigation(authViewModel,context)

            }
            }
        }
    }


