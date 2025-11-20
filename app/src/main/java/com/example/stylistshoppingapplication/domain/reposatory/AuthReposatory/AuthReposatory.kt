package com.example.stylistshoppingapplication.domain.reposatory.AuthReposatory

import com.example.stylistshoppingapplication.domain.util.Results
import com.google.firebase.auth.AuthCredential

interface AuthReposatory {

    suspend fun login(email:String,password:String): Results<String>
    suspend fun signup(email:String,password: String):Results<String>
    suspend fun signInWithGoogle(credential: AuthCredential): Results<String>
    suspend fun getCurrentUser(): com.google.firebase.auth.FirebaseUser?

}