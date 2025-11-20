package com.example.stylistshoppingapplication.presentation.ViewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylistshoppingapplication.data.remote.Apiservices.Apiservices
import com.example.stylistshoppingapplication.data.remote.Dto.Product
import com.example.stylistshoppingapplication.data.reposatoryImp.ProductRepositoryImp
import com.example.stylistshoppingapplication.domain.model.ProductModel
import com.example.stylistshoppingapplication.domain.reposatory.ProductReposatory.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ProductViewModel : ViewModel() {
    private val apiService = Apiservices()
    private val repository: ProductRepository = ProductRepositoryImp(apiService)

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            try {
                val products = repository.getAllProduct()
                if (products.isEmpty()) {
                    _uiState.value = ProductUiState.Error("No products found")
                } else {
                    _uiState.value = ProductUiState.Success(
                        featuredProducts = products.take(30),
                        trendingProducts = products.shuffled().take(30)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error("Failed to load products: ${e.localizedMessage}")
            }
        }
    }

    sealed class ProductUiState {
        object Loading : ProductUiState()
        data class Success(
            val featuredProducts: List<ProductModel>,
            val trendingProducts: List<ProductModel>
        ) : ProductUiState()
        data class Error(val message: String) : ProductUiState()
    }
}
