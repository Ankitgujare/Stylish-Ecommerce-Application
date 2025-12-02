package com.example.stylistshoppingapplication.presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylistshoppingapplication.data.remote.Apiservices.StoreApiService
import com.example.stylistshoppingapplication.data.remote.Dto.Store_Products_Dto.toDomain
import com.example.stylistshoppingapplication.domain.model.ProductModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {
    private val apiService by lazy { StoreApiService() }

    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    fun fetchProductsByCategory(category: String) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                _uiState.value = CategoryUiState.Loading
                val products = apiService.getProductsByCategory(category)
                if (products.isEmpty()) {
                    _uiState.value = CategoryUiState.Error("No products found in this category")
                } else {
                    _uiState.value = CategoryUiState.Success(
                        products = products.map { it.toDomain() }
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = CategoryUiState.Error("Failed to load products: ${e.localizedMessage}")
            }
        }
    }

    sealed class CategoryUiState {
        object Loading : CategoryUiState()
        data class Success(val products: List<ProductModel>) : CategoryUiState()
        data class Error(val message: String) : CategoryUiState()
    }
}
