package com.example.stylistshoppingapplication.data.remote.Dto

import kotlinx.serialization.Serializable

@Serializable
data class Dimensions(
    val depth: Double,
    val height: Double,
    val width: Double
)