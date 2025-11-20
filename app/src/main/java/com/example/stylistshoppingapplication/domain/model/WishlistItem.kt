package com.example.stylistshoppingapplication.domain.model

data class WishlistItem(
    val product: ProductModel,
    val addedDate: Long = System.currentTimeMillis()
)
