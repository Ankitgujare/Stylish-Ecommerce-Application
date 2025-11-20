package com.example.stylistshoppingapplication.data.remote.Dto

import com.example.stylistshoppingapplication.domain.model.ProductListModel
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val limit: Int,
    val products: List<Product>,
    val skip: Int,
    val total: Int
)

fun ProductDto.toDomain(): ProductListModel {
    return ProductListModel(
        products = products.map { it.toDomain() },
        limit = limit,
        skip = skip,
        total = total
    )
}
