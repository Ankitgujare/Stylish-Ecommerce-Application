package com.example.stylistshoppingapplication.presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylistshoppingapplication.domain.model.CartItem
import com.example.stylistshoppingapplication.domain.model.ProductModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _cartTotal = MutableStateFlow(0.0)
    val cartTotal: StateFlow<Double> = _cartTotal.asStateFlow()

    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    init {
        updateCartTotals()
    }

    fun addToCart(product: ProductModel, quantity: Int = 1, selectedSize: String = "", selectedColor: String = "") {
        viewModelScope.launch {
            val existingItem = _cartItems.value.find { it.product.id == product.id }
            
            if (existingItem != null) {
                // Update existing item quantity
                val updatedItems = _cartItems.value.map { item ->
                    if (item.product.id == product.id) {
                        item.copy(quantity = item.quantity + quantity)
                    } else {
                        item
                    }
                }
                _cartItems.value = updatedItems
            } else {
                // Add new item
                val newItem = CartItem(
                    product = product,
                    quantity = quantity,
                    selectedSize = selectedSize,
                    selectedColor = selectedColor
                )
                _cartItems.value = _cartItems.value + newItem
            }
            updateCartTotals()
        }
    }

    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            _cartItems.value = _cartItems.value.filter { it.product.id != productId }
            updateCartTotals()
        }
    }

    fun updateQuantity(productId: Int, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity <= 0) {
                removeFromCart(productId)
            } else {
                val updatedItems = _cartItems.value.map { item ->
                    if (item.product.id == productId) {
                        item.copy(quantity = newQuantity)
                    } else {
                        item
                    }
                }
                _cartItems.value = updatedItems
                updateCartTotals()
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            _cartItems.value = emptyList()
            updateCartTotals()
        }
    }

    private fun updateCartTotals() {
        val total = _cartItems.value.sumOf { it.totalPrice }
        val itemCount = _cartItems.value.sumOf { it.quantity }
        
        _cartTotal.value = total
        _cartItemCount.value = itemCount
    }

    fun isProductInCart(productId: Int): Boolean {
        return _cartItems.value.any { it.product.id == productId }
    }

    fun getCartItemQuantity(productId: Int): Int {
        return _cartItems.value.find { it.product.id == productId }?.quantity ?: 0
    }
}
