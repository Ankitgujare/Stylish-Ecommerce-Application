package com.store.ecommerceapplication.domain.usecase

import com.example.stylistshoppingapplication.domain.reposatory.AuthReposatory.AuthReposatory
import com.example.stylistshoppingapplication.domain.util.Results

class LoginUseCase(private val repository: AuthReposatory) {

    suspend operator fun invoke(email:String,password:String): Results<String> {
        return  repository.login(email,password)
    }
}