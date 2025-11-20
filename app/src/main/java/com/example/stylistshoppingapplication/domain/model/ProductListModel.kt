package com.example.stylistshoppingapplication.domain.model

data class ProductListModel(
    val products: List<ProductModel>,
    val limit: Int,
    val skip: Int,
    val total: Int
)