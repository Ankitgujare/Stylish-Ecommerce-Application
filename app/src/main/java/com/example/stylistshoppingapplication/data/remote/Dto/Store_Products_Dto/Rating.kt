package com.example.stylistshoppingapplication.data.remote.Dto.Store_Products_Dto

import kotlinx.serialization.Serializable

@Serializable
data class Rating(
    val count: Int,
    val rate: Double
)