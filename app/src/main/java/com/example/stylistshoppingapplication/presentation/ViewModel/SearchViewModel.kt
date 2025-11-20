package com.example.stylistshoppingapplication.presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylistshoppingapplication.domain.model.ProductModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<ProductModel>>(emptyList())
    val searchResults: StateFlow<List<ProductModel>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _allProducts = MutableStateFlow<List<ProductModel>>(emptyList())
    val allProducts: StateFlow<List<ProductModel>> = _allProducts.asStateFlow()

    fun setAllProducts(products: List<ProductModel>) {
        _allProducts.value = products
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotEmpty()) {
            searchProducts(query)
        } else {
            _searchResults.value = emptyList()
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            
            val filteredProducts = _allProducts.value.filter { product ->
                product.title.contains(query, ignoreCase = true) ||
                product.brand?.contains(query, ignoreCase = true) == true ||
                product.category.contains(query, ignoreCase = true) ||
                product.description.contains(query, ignoreCase = true)
            }
            
            _searchResults.value = filteredProducts
            _isSearching.value = false
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }
}
