package com.example.stylistshoppingapplication.domain.model

data class ReviewModel(
    val comment: String,
    val date: String,
    val rating: Int,
    val reviewerName: String
)