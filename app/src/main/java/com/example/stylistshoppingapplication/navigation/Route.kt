package com.example.stylistshoppingapplication.navigation

import kotlinx.serialization.Serializable



sealed class Routes(val route: String) {
    object homescreen : Routes("homescreen")
    object loginScreen : Routes("loginscreen")
    object signupScreen : Routes("signupscreen")
    object onborardingScreen : Routes("onboardingscreen")
    object splashScreen : Routes("splashscreen")
    object forgetpassword : Routes("forgetpass")
    object getstarted:Routes("getstartedScreen")
    object viewAllProduct:Routes("viewAllProduct")
    object cartScreen:Routes("cart")
    object wishlistScreen:Routes("wishlist")
    object searchScreen:Routes("search")
    object checkoutScreen:Routes("checkout")
    object placeOrderScreen:Routes("placeorder")
    object paymentSuccessScreen:Routes("paymentsuccess")
    object profileScreen:Routes("profile")
    object settingsScreen:Routes("settings")
    data class ProductDetailScreen(val productId: Int) : Routes("productDetaileScreen")
}