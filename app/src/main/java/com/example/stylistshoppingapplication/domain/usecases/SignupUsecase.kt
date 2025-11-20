package com.example.stylistshoppingapplication.domain.usecases

import com.example.stylistshoppingapplication.domain.reposatory.AuthReposatory.AuthReposatory
import com.example.stylistshoppingapplication.domain.util.Results

class SignupUsecase(private val repository: AuthReposatory){

    suspend operator fun invoke(email:String,password:String): Results<String> {

        if (email.isBlank()){
            return Results.Failure("Email cannot be empty")
        }

        if (password.isBlank()){
            return Results.Failure("password cannot be empty")
        }

        if (!email.contains("@") || !email.contains(".")){
             return Results.Failure("Invalid Email Formate")
        }

        return repository.signup(email,password)

    }
}