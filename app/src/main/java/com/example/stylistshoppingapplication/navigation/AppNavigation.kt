package com.example.stylistshoppingapplication.navigation


import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stylistshoppingapplication.presentation.ViewModel.AuthViewModel
import com.example.stylistshoppingapplication.presentation.screens.SplashScreen
import com.example.stylistshoppingapplication.presentation.screens.HomeScreen
import com.example.stylistshoppingapplication.presentation.ViewModel.ProductViewModel
import com.example.stylistshoppingapplication.presentation.screens.ProductDetailScreen
import com.example.stylistshoppingapplication.presentation.screens.CartScreen
import com.example.stylistshoppingapplication.presentation.screens.LoginScreen
import com.example.stylistshoppingapplication.presentation.ViewModel.CartViewModel
import com.example.stylistshoppingapplication.presentation.ViewModel.viewModelFactory.CartViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun AppNavigation(authViewModel: AuthViewModel, context: Context) {
    val navController = rememberNavController()
    val productViewModel: ProductViewModel = viewModel()
    
    // Create CartViewModel with factory
    val cartViewModel: CartViewModel = viewModel(factory = CartViewModelFactory(context))
    
    // Create WishlistViewModel and SearchViewModel
    val wishlistViewModel: com.example.stylistshoppingapplication.presentation.ViewModel.WishlistViewModel = viewModel()
    val searchViewModel: com.example.stylistshoppingapplication.presentation.ViewModel.SearchViewModel = viewModel()
    
    // DataStore
    val dataStore = remember { com.example.stylistshoppingapplication.data.local.UserPrefrenceDataStore(context) }

    // Create ProfileViewModel
    val profileViewModel: com.example.stylistshoppingapplication.presentation.ViewModel.ProfileViewModel = viewModel(
        factory = com.example.stylistshoppingapplication.presentation.ViewModel.viewModelFactory.ProfileViewModelFactory(context)
    )

    NavHost(navController = navController, startDestination = "splash") {
        composable("home") {
            HomeScreen(
                productViewModel = productViewModel,
                navController = navController,
                cartViewModel = cartViewModel
            )
        }
// ... (rest of the file until profile composable)
        composable("profile") {
            com.example.stylistshoppingapplication.presentation.screens.ProfileScreen(
                navController = navController,
                viewModel = profileViewModel
            )
        }

        composable("splash") {
            SplashScreenWithNavigation(navController, dataStore)
        }
        
        composable("onboarding") {
            com.example.stylistshoppingapplication.presentation.screens.OnboardingScreen(
                navController = navController,
                onFinish = {
                    // Set first time login to false
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                        dataStore.setFirstTimeLogin(false)
                    }
                    // Navigate to Login
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) {
            val productId = it.arguments?.getInt("productId") ?: 0
            ProductDetailScreen(
                navController = navController,
                productId = productId,
                productViewModel = productViewModel,
                cartViewModel = cartViewModel,
                wishlistViewModel = wishlistViewModel
            )
        }

        composable("cart") {
            CartScreen(
                navController = navController,
                cartViewModel = cartViewModel
            )
        }

        composable("checkout") {
            com.example.stylistshoppingapplication.presentation.screens.CheckoutScreen(
                navController = navController,
                cartViewModel = cartViewModel
            )
        }

        composable("payment") {
            com.example.stylistshoppingapplication.presentation.screens.PaymentScreen(
                navController = navController,
                cartViewModel = cartViewModel
            )
        }
        
        composable("login") {
            LoginScreen(
                navController = navController,
                onRegister = { 
                    navController.navigate("signup")
                },
                onForgot = { 
                    navController.navigate("forgot_password")
                },
                authViewModel = authViewModel
            )
        }
        
        composable("signup") {
            com.example.stylistshoppingapplication.presentation.screens.SignUpScreen(
                navController = navController,
                onLoginClick = {
                    navController.popBackStack()
                },
                authViewModel = authViewModel
            )
        }
        
        composable("forgot_password") {
            com.example.stylistshoppingapplication.presentation.screens.ForgotPasswordScreen(
                navController = navController,
                onSubmit = { email ->
                    // Handle password reset logic here (e.g., call ViewModel)
                    // For now, just pop back
                    navController.popBackStack()
                }
            )
        }
        
        composable("wishlist") {
            com.example.stylistshoppingapplication.presentation.screens.WishlistScreen(
                navController = navController,
                wishlistViewModel = wishlistViewModel,
                cartViewModel = cartViewModel
            )
        }
        
        composable("search") {
            com.example.stylistshoppingapplication.presentation.screens.SearchScreen(
                navController = navController,
                searchViewModel = searchViewModel,
                productViewModel = productViewModel
            )
        }
        
        composable("settings") {
            com.example.stylistshoppingapplication.presentation.screens.SettingsScreen(
                navController = navController
            )
        }

        composable("featured_products") {
            com.example.stylistshoppingapplication.presentation.screens.FeaturedProductScreen(
                navController = navController,
                productViewModel = productViewModel,
                cartViewModel = cartViewModel
            )
        }

        composable("trending_products") {
            com.example.stylistshoppingapplication.presentation.screens.TrendingProductScreen(
                navController = navController,
                productViewModel = productViewModel,
                cartViewModel = cartViewModel
            )
        }

        composable(
            route = "category_products/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            com.example.stylistshoppingapplication.presentation.screens.CategoryProductScreen(
                category = category,
                navController = navController,
                cartViewModel = cartViewModel
            )
        }
        
        composable("profile") {
            com.example.stylistshoppingapplication.presentation.screens.ProfileScreen(
                navController = navController,
                viewModel = profileViewModel
            )
        }
    }
}

@Composable
fun SplashScreenWithNavigation(
    navController: NavController,
    dataStore: com.example.stylistshoppingapplication.data.local.UserPrefrenceDataStore
) {
    SplashScreen()
    
    val isFirstTime = dataStore.isFirsttimeLogin.collectAsState(initial = true)
    val isLoggedIn = dataStore.isLogin.collectAsState(initial = false)
    
    // Navigation from splash
    LaunchedEffect(isFirstTime.value, isLoggedIn.value) {
        delay(2000) // 2 seconds splash
        
        if (isFirstTime.value) {
            navController.navigate("onboarding") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            if (isLoggedIn.value) {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            } else {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }
}


