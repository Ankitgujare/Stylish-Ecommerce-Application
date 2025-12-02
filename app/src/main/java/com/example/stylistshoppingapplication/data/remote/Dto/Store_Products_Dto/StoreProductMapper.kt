package com.example.stylistshoppingapplication.data.remote.Dto.Store_Products_Dto

import com.example.stylistshoppingapplication.domain.model.ProductModel
import com.example.stylistshoppingapplication.domain.model.DimensionsModel
import com.example.stylistshoppingapplication.domain.model.ReviewModel

fun EcomerceProductsItem.toDomain(): ProductModel {
    return ProductModel(
        id = this.id,
        title = this.title,
        description = this.description,
        price = this.price,
        discountPercentage = 0.0, // FakeStore API doesn't have discount
        rating = this.rating.rate,
        stock = this.rating.count, // Using count as stock
        brand = null,
        category = this.category,
        thumbnail = this.image,
        images = listOf(this.image), // Single image
        minimumOrderQuantity = 1,
        returnPolicy = "30 days return policy",
        tags = listOf(this.category),
        warrantyInformation = "1 year warranty",
        dimensions = DimensionsModel(0.0, 0.0, 0.0), // No dimensions in FakeStore API
        reviews = emptyList() // No reviews in FakeStore API
    )
}
