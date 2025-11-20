package com.example.stylistshoppingapplication.data.reposatoryImp

import com.example.stylistshoppingapplication.domain.reposatory.AuthReposatory.AuthReposatory
import com.example.stylistshoppingapplication.domain.util.Results
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.tasks.await

class AuthReposatoryImp(
   private val firebaseAuth: FirebaseAuth
):AuthReposatory {
    override suspend fun login(email: String, password: String): Results<String> {
        return try{
            firebaseAuth.signInWithEmailAndPassword(email,password).await()
            Results.Success("Login Successful")
        }catch (e:Exception){
            Results.Failure(e.localizedMessage?:"Unknown Error During Login")
        }


    }

    override suspend fun signup(email: String, password: String): Results<String> {

        return try{
            firebaseAuth.createUserWithEmailAndPassword(email,password).await()
            Results.Success("Signup Successful")
        }catch (e:Exception){
            Results.Failure("Signup Failed")
        }
    }

    override suspend fun signInWithGoogle(credential: AuthCredential): Results<String> {
        return try {
            firebaseAuth.signInWithCredential(credential).await()
            Results.Success("Google Sign-In Successful")
        } catch (e: Exception) {
            Results.Failure(e.localizedMessage ?: "Google Sign-In Failed")
        }
    }

    override suspend fun getCurrentUser(): com.google.firebase.auth.FirebaseUser? {
        return firebaseAuth.currentUser
    }

}