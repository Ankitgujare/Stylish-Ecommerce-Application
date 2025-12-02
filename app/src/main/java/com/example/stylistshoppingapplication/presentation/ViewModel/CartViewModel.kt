package com.example.stylistshoppingapplication.presentation.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylistshoppingapplication.data.local.repository.CartRepository
import com.example.stylistshoppingapplication.domain.model.CartItem
import com.example.stylistshoppingapplication.domain.model.ProductModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartViewModel(context: Context) : ViewModel() {
    private val repository = CartRepository(context)
    
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _cartTotal = MutableStateFlow(0.0)
    val cartTotal: StateFlow<Double> = _cartTotal.asStateFlow()

    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    private val _checkoutItems = MutableStateFlow<List<CartItem>>(emptyList())
    val checkoutItems: StateFlow<List<CartItem>> = _checkoutItems.asStateFlow()

    private val _checkoutTotal = MutableStateFlow(0.0)
    val checkoutTotal: StateFlow<Double> = _checkoutTotal.asStateFlow()

    init {
        loadCartItems()
        updateCartTotals()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            repository.getAllCartItems()
                .catch { e ->
                    e.printStackTrace()
                    emit(emptyList())
                }
                .collectLatest { cartItems ->
                    _cartItems.value = cartItems
                    updateCartTotals()
                }
        }
    }

    fun addToCart(product: ProductModel, quantity: Int = 1, selectedSize: String = "", selectedColor: String = "") {
        viewModelScope.launch {
            val existingItem = _cartItems.value.find { it.product.id == product.id }
            
            if (existingItem != null) {
                // Update existing item quantity
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
                repository.updateCartItem(updatedItem)
                _cartItems.value = _cartItems.value.map { item ->
                    if (item.product.id == product.id) {
                        updatedItem
                    } else {
                        item
                    }
                }
            } else {
                // Add new item
                val newItem = CartItem(
                    product = product,
                    quantity = quantity,
                    selectedSize = selectedSize,
                    selectedColor = selectedColor
                )
                repository.insertCartItem(newItem)
                _cartItems.value = _cartItems.value + newItem
            }
            updateCartTotals()
        }
    }

    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            repository.deleteCartItem(productId)
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
                        val updatedItem = item.copy(quantity = newQuantity)
                        repository.updateCartItem(updatedItem)
                        updatedItem
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
            repository.clearCart()
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

    fun prepareCheckout(items: List<CartItem>) {
        _checkoutItems.value = items
        _checkoutTotal.value = items.sumOf { it.totalPrice }
    }
}