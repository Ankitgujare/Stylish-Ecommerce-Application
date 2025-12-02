package com.example.stylistshoppingapplication.navigation

import kotlinx.serialization.Serializable


sealed class Routes(val route: String) {
    object homescreen : Routes("home")
    object splashScreen : Routes("splash")
    object cartScreen : Routes("cart")
    object profileScreen : Routes("profile")
    object wishlistScreen : Routes("wishlist")
    object searchScreen : Routes("search")
    object settingsScreen : Routes("settings")
    object loginScreen : Routes("login")
    object featuredProductScreen : Routes("featured_products")
    object trendingProductScreen : Routes("trending_products")
    object categoryProductScreen : Routes("category_products/{category}") {
        fun createRoute(category: String) = "category_products/$category"
    }
}