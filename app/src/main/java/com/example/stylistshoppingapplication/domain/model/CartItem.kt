package com.example.stylistshoppingapplication.domain.model

data class CartItem(
    val product: ProductModel,
    val quantity: Int = 1,
    val selectedSize: String = "",
    val selectedColor: String = ""
) {
    val totalPrice: Double
        get() = product.price * quantity
}
