package com.example.stylistshoppingapplication.presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylistshoppingapplication.domain.model.ProductModel
import com.example.stylistshoppingapplication.domain.model.WishlistItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WishlistViewModel : ViewModel() {
    private val _wishlistItems = MutableStateFlow<List<WishlistItem>>(emptyList())
    val wishlistItems: StateFlow<List<WishlistItem>> = _wishlistItems.asStateFlow()

    private val _wishlistCount = MutableStateFlow(0)
    val wishlistCount: StateFlow<Int> = _wishlistCount.asStateFlow()

    init {
        updateWishlistCount()
    }

    fun addToWishlist(product: ProductModel) {
        viewModelScope.launch {
            if (!isProductInWishlist(product.id)) {
                val newItem = WishlistItem(product = product)
                _wishlistItems.value = _wishlistItems.value + newItem
                updateWishlistCount()
            }
        }
    }

    fun removeFromWishlist(productId: Int) {
        viewModelScope.launch {
            _wishlistItems.value = _wishlistItems.value.filter { it.product.id != productId }
            updateWishlistCount()
        }
    }

    fun toggleWishlist(product: ProductModel) {
        viewModelScope.launch {
            if (isProductInWishlist(product.id)) {
                removeFromWishlist(product.id)
            } else {
                addToWishlist(product)
            }
        }
    }

    fun clearWishlist() {
        viewModelScope.launch {
            _wishlistItems.value = emptyList()
            updateWishlistCount()
        }
    }

    fun isProductInWishlist(productId: Int): Boolean {
        return _wishlistItems.value.any { it.product.id == productId }
    }

    private fun updateWishlistCount() {
        _wishlistCount.value = _wishlistItems.value.size
    }
}
