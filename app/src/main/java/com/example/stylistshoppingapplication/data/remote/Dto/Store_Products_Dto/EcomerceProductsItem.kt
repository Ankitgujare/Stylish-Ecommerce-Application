package com.example.stylistshoppingapplication.data.remote.Dto.Store_Products_Dto

import kotlinx.serialization.Serializable

@Serializable
data class EcomerceProductsItem(
    val category: String,
    val description: String,
    val id: Int,
    val image: String,
    val price: Double,
    val rating: Rating,
    val title: String
)