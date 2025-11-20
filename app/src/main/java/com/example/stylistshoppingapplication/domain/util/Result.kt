package com.example.stylistshoppingapplication.domain.util


sealed class Results<out T>{
    data object Ideal:Results<Nothing>()
    data object Loading:Results<Nothing>()
    data class Success<out T>(val data: T):Results<T>()
    data class Failure(val message:String):Results<Nothing>()

}