package com.example.stylistshoppingapplication.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey val productId: Int,
    val title: String,
    val price: Double,
    val image: String,
    val quantity: Int,
    val selectedSize: String = "",
    val selectedColor: String = ""
)