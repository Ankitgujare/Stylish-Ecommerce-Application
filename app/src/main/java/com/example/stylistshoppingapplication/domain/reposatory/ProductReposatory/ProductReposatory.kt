package com.example.stylistshoppingapplication.domain.reposatory.ProductReposatory

import com.example.stylistshoppingapplication.data.remote.Dto.ProductDto
import com.example.stylistshoppingapplication.domain.model.ProductModel


interface ProductRepository {
    suspend fun getAllProduct(): List<ProductModel>
}