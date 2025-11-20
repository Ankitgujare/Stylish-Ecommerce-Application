package com.example.stylistshoppingapplication.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val photoUrl: String?,
    val pincode: String,
    val address: String,
    val city: String,
    val state: String,
    val country: String,
    val bankAccount: String,
    val accountHolder: String,
    val ifscCode: String
)