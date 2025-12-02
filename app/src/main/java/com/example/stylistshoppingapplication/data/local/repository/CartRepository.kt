package com.example.stylistshoppingapplication.data.local.repository

import android.content.Context
import com.example.stylistshoppingapplication.data.local.database.CartDatabase
import com.example.stylistshoppingapplication.data.local.model.CartEntity
import com.example.stylistshoppingapplication.domain.model.CartItem
import com.example.stylistshoppingapplication.domain.model.ProductModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepository(context: Context) {
    private val cartDatabase = CartDatabase.getDatabase(context)
    private val cartDao = cartDatabase.cartDao()

    fun getAllCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems().map { cartEntities ->
            cartEntities.map { entity ->
                // Convert CartEntity to CartItem
                // Since we don't have the full ProductModel in the database, we create a minimal one
                val product = ProductModel(
                    id = entity.productId,
                    title = entity.title,
                    description = "",
                    price = entity.price,
                    discountPercentage = 0.0,
                    rating = 0.0,
                    stock = 0,
                    brand = null,
                    category = "",
                    thumbnail = entity.image,
                    images = emptyList(),
                    minimumOrderQuantity = 1,
                    returnPolicy = "",
                    tags = emptyList(),
                    warrantyInformation = "",
                    dimensions = com.example.stylistshoppingapplication.domain.model.DimensionsModel(0.0, 0.0, 0.0),
                    reviews = emptyList()
                )
                CartItem(
                    product = product,
                    quantity = entity.quantity,
                    selectedSize = entity.selectedSize,
                    selectedColor = entity.selectedColor
                )
            }
        }
    }

    suspend fun insertCartItem(cartItem: CartItem) {
        val cartEntity = CartEntity(
            productId = cartItem.product.id,
            title = cartItem.product.title,
            price = cartItem.product.price,
            image = cartItem.product.thumbnail,
            quantity = cartItem.quantity,
            selectedSize = cartItem.selectedSize,
            selectedColor = cartItem.selectedColor
        )
        cartDao.insertCartItem(cartEntity)
    }

    suspend fun updateCartItem(cartItem: CartItem) {
        val cartEntity = CartEntity(
            productId = cartItem.product.id,
            title = cartItem.product.title,
            price = cartItem.product.price,
            image = cartItem.product.thumbnail,
            quantity = cartItem.quantity,
            selectedSize = cartItem.selectedSize,
            selectedColor = cartItem.selectedColor
        )
        cartDao.updateCartItem(cartEntity)
    }

    suspend fun deleteCartItem(productId: Int) {
        cartDao.deleteCartItem(productId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    suspend fun getCartItemCount(): Int {
        return cartDao.getCartItemCount()
    }
}