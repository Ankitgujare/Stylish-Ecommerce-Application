package com.example.stylistshoppingapplication.domain.model

import kotlinx.serialization.descriptors.PrimitiveKind

data class UserPrefrenceState(
    val isFirstTimeLogedIn:Boolean=true,
    val isLogedIn:Boolean=false,
    val isDarkMode:Boolean=false,
    val isLoading:Boolean=true
)