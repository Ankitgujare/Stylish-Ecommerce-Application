package com.example.stylistshoppingapplication.data.reposatoryImp

import com.example.stylistshoppingapplication.data.remote.Apiservices.Apiservices
import com.example.stylistshoppingapplication.data.remote.Dto.ProductDto
import com.example.stylistshoppingapplication.data.remote.Dto.toDomain
import com.example.stylistshoppingapplication.domain.model.ProductModel
import com.example.stylistshoppingapplication.domain.reposatory.ProductReposatory.ProductRepository


class ProductRepositoryImp(
    private val apiService: Apiservices
) : ProductRepository {
    override suspend fun getAllProduct(limit: Int): List<ProductModel> {
        val dto = apiService.getAllProduct(limit)
        return dto.products.map { it.toDomain() } // map DTO -> Model
    }
}