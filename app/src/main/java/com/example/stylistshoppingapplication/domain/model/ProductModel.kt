package com.example.stylistshoppingapplication.domain.model

import com.example.stylistshoppingapplication.data.remote.Dto.Product


data class ProductModel(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val brand: String?,
    val category: String,
    val thumbnail: String,
    val images: List<String>,
    val minimumOrderQuantity:Int,
    val returnPolicy:String,
    val tags: List<String>,
    val warrantyInformation: String,
    val dimensions: DimensionsModel,
    val reviews: List<ReviewModel>,
)
