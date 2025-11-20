package com.example.stylistshoppingapplication.domain.usecases

import com.example.stylistshoppingapplication.domain.reposatory.AuthReposatory.AuthReposatory
import com.example.stylistshoppingapplication.domain.util.Results
import com.google.firebase.auth.AuthCredential

class GoogleSignInUseCase(
    private val authRepository: AuthReposatory
) {
    suspend operator fun invoke(credential: AuthCredential): Results<String> {
        return authRepository.signInWithGoogle(credential)
    }
}
