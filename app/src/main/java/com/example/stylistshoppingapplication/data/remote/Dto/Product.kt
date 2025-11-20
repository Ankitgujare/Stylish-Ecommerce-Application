package com.example.stylistshoppingapplication.data.remote.Dto

import com.example.stylistshoppingapplication.domain.model.DimensionsModel
import com.example.stylistshoppingapplication.domain.model.ProductModel
import com.example.stylistshoppingapplication.domain.model.ReviewModel
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val availabilityStatus: String,
    val brand: String?,
    val category: String,
    val description: String,
    val dimensions: Dimensions,
    val discountPercentage: Double,
    val id: Int,
    val images: List<String>,
    val meta: Meta,
    val minimumOrderQuantity: Int,
    val price: Double,
    val rating: Double,
    val returnPolicy: String,
    val reviews: List<Review>,
    val shippingInformation: String,
    val sku: String,
    val stock: Int,
    val tags: List<String>,
    val thumbnail: String,
    val title: String,
    val warrantyInformation: String,
    val weight: Int
)


fun Product.toDomain(): ProductModel {
    return ProductModel(
        id = id,
        title = title,
        description = description,
        price = price,
        discountPercentage = discountPercentage,
        rating = rating,
        stock = stock,
        brand = brand,
        category = category,
        thumbnail = thumbnail,
        images = images,
        minimumOrderQuantity = minimumOrderQuantity,
        returnPolicy = returnPolicy,
        tags = tags,
        warrantyInformation = warrantyInformation,

        // ✅ map dimensions and reviews
        dimensions = DimensionsModel(
            depth = dimensions.depth,
            height = dimensions.height,
            width = dimensions.width
        ),
        reviews = reviews.map {
            ReviewModel(
                comment = it.comment,
                date = it.date,
                rating = it.rating,
                reviewerName = it.reviewerName
            )
        }

    )
}




