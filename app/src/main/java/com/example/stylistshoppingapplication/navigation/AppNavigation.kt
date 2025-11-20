package com.example.stylistshoppingapplication.navigation


import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stylish.ui.screens.OnboardingScreen
import com.example.stylish.ui.screens.PaymentSuccessScreen
import com.example.stylistshoppingapplication.data.local.UserPrefrenceDataStore
import com.example.stylistshoppingapplication.data.reposatoryImp.UserPrefrenceImplementation
import com.example.stylistshoppingapplication.domain.reposatory.userPreferenceReposatory.UserPreferenceReposatory
import com.example.stylistshoppingapplication.domain.usecases.GetUserPreferencesUseCase
import com.example.stylistshoppingapplication.domain.usecases.SetUserPrefrenceUseCase
import com.example.stylistshoppingapplication.domain.util.Results
import com.example.stylistshoppingapplication.presentation.ViewModel.AuthViewModel
import com.example.stylistshoppingapplication.presentation.screens.SplashScreen
import com.example.stylistshoppingapplication.presentation.screens.HomeScreen
import com.example.stylistshoppingapplication.presentation.ViewModel.ProductViewModel
import com.example.stylistshoppingapplication.presentation.ViewModel.userPrefrenceViewModel.UserPrefrenceViewModel
import com.example.stylistshoppingapplication.presentation.ViewModel.viewModelFactory.AuthViewModelFactory
import com.example.stylistshoppingapplication.presentation.screens.GetstartedScreen
import com.example.stylistshoppingapplication.presentation.screens.ProductDetailScreen
import com.example.stylistshoppingapplication.presentation.screens.ViewAllProductScreen
import com.example.stylistshoppingapplication.presentation.screens.CartScreen
import com.example.stylistshoppingapplication.presentation.screens.WishlistScreen
import com.example.stylistshoppingapplication.presentation.screens.SearchScreen
import com.example.stylistshoppingapplication.presentation.screens.CheckoutScreen
import com.example.stylistshoppingapplication.presentation.screens.PlaceOrderScreen
import com.example.stylistshoppingapplication.presentation.screens.ProfileScreen
import com.example.stylistshoppingapplication.presentation.screens.SettingsScreen
import com.example.stylistshoppingapplication.presentation.screens.SignUpScreen
import com.example.stylistshoppingapplication.presentation.ViewModel.CartViewModel
import com.example.stylistshoppingapplication.presentation.ViewModel.WishlistViewModel
import com.example.stylistshoppingapplication.presentation.ViewModel.SearchViewModel
import com.example.stylistshoppingapplication.presentation.screens.LoginScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Route


@Composable
fun AppNavigation(authViewModel: AuthViewModel,context: Context) {
    val navController = rememberNavController()
    val productViewModel: ProductViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val wishlistViewModel: WishlistViewModel = viewModel()
    val searchViewModel: SearchViewModel = viewModel()

    // Create dependencies ONCE
    val userPreferencesDataStore = remember { UserPrefrenceDataStore(context) }
    val UserPreferenceReposatory= remember { UserPrefrenceImplementation(userPreferencesDataStore) }
    val getUserPreferencesUseCase= remember { GetUserPreferencesUseCase(UserPreferenceReposatory) }
    val setUserPrefrenceUseCase= remember { SetUserPrefrenceUseCase(UserPreferenceReposatory) }
    val UserPreferenceViewModel= remember { UserPrefrenceViewModel(getUserPreferencesUseCase,setUserPrefrenceUseCase) }


    // Observe user preferences state
    val userPreferencesState by UserPreferenceViewModel.state.collectAsState()




    NavHost(navController = navController, startDestination = Routes.splashScreen.route) {
        composable(Routes.homescreen.route) {
            HomeScreen(
                productViewModel = productViewModel,
                navController = navController,
                cartViewModel = cartViewModel,
                wishlistViewModel = wishlistViewModel,
                searchViewModel = searchViewModel
            )
        }


            composable(Routes.splashScreen.route) {
                SplashScreen()
            }

        composable(Routes.getstarted.route) {
            GetstartedScreen(Getstarted = {
                navController.navigate(Routes.homescreen.route)
            })
        }


        composable(Routes.loginScreen.route) {
            LoginScreen(
                navController,
                onRegister = {
                    navController.navigate(Routes.signupScreen.route)
                },
                onForgot = {
                    navController.navigate(Routes.forgetpassword.route)
                },
                //Passing the authViewModel
                authViewModel
            )
        }


        composable(Routes.onborardingScreen.route){
            OnboardingScreen(onGetStarted = {
               navController.navigate(Routes.homescreen.route) {
                   popUpTo(Routes.onborardingScreen.route) { inclusive = true }
               }
            },
            onSkip = {
                navController.navigate(Routes.homescreen.route) {
                    popUpTo(Routes.onborardingScreen.route) { inclusive = true }
                }
            }
            )
        }


        composable(Routes.signupScreen.route){
            SignUpScreen(navController,authViewModel)
        }

        composable(Routes.viewAllProduct.route){
            ViewAllProductScreen(productViewModel,navController)
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

        composable(Routes.cartScreen.route) {
            CartScreen(
                navController = navController,
                cartViewModel = cartViewModel
            )
        }

        composable(Routes.wishlistScreen.route) {
            WishlistScreen(
                navController = navController,
                wishlistViewModel = wishlistViewModel,
                cartViewModel = cartViewModel
            )
        }

        composable(Routes.searchScreen.route) {
            SearchScreen(
                navController = navController,
                searchViewModel = searchViewModel,
                productViewModel = productViewModel
            )
        }

        composable(Routes.checkoutScreen.route) {
            CheckoutScreen(
                navController = navController,
                cartViewModel = cartViewModel
            )
        }

        composable(Routes.placeOrderScreen.route) {
            PlaceOrderScreen(
                navController = navController,
                cartViewModel = cartViewModel
            )
        }

        composable(Routes.paymentSuccessScreen.route) {
            PaymentSuccessScreen(
                onClose = {
                    navController.navigate(Routes.homescreen.route) {
                        popUpTo(Routes.homescreen.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.profileScreen.route) {
            ProfileScreen(navController = navController)
        }

        composable(Routes.settingsScreen.route) {
            SettingsScreen(navController = navController)
        }




    }
    LaunchedEffect(
        userPreferencesState.isLoading,
        userPreferencesState.isLogedIn,
        userPreferencesState.isFirstTimeLogedIn
    ) {
        // Wait for loading to complete
        if (!userPreferencesState.isLoading) {
            // Add a small delay for splash screen
            delay(2000) // 2 seconds splash

            val destination = when {
                // Case 3: User is logged in and not first time -> Home
                userPreferencesState.isLogedIn && !userPreferencesState.isFirstTimeLogedIn -> {
                    Routes.homescreen.route
                }
                // Case 2: User is logged in but first time -> Onboarding
                userPreferencesState.isLogedIn && userPreferencesState.isFirstTimeLogedIn -> {
                    Routes.onborardingScreen.route
                }
                // Case 1: User not logged in -> Login
                else -> {
                    Routes.loginScreen.route
                }
            }

            navController.navigate(destination) {
                popUpTo(Routes.splashScreen.route) { inclusive = true }
            }
        }
    }
    
    // Handle navigation after successful authentication
    val authState = authViewModel.authState.collectAsState().value
    LaunchedEffect(authState) {
        when (authState) {
            is Results.Success -> {
                // After successful authentication, let the user preferences logic handle navigation
                // The user preferences will be updated by the AuthViewModel
            }
            else -> {}
        }
    }

}


