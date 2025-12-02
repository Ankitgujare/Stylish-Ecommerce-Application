package com.example.stylistshoppingapplication.data.remote.Apiservices

import com.example.stylistshoppingapplication.data.remote.Dto.Store_Products_Dto.EcomerceProductsItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class StoreApiService {
    @OptIn(ExperimentalSerializationApi::class)
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                coerceInputValues = true
                explicitNulls = false
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }

        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "fakestoreapi.com"
            }
        }
    }

    suspend fun getAllProducts(): List<EcomerceProductsItem> {
        return client.get("/products").body()
    }

    suspend fun getProductsByCategory(category: String): List<EcomerceProductsItem> {
        return client.get("/products/category/$category").body()
    }
}
