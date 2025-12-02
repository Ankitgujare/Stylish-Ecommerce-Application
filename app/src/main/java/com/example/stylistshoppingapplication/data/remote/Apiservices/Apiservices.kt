package com.example.stylistshoppingapplication.data.remote.Apiservices
import com.example.stylistshoppingapplication.data.remote.Dto.ProductDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class Apiservices {
    @OptIn(ExperimentalSerializationApi::class)
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true  // Allows malformed JSON
                coerceInputValues = true  // Coerces nulls for non-nullable fields
                explicitNulls = false  // Tolerates missing nulls
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
                host = "dummyjson.com"  // Correct host
            }
        }
    }


    suspend fun getAllProduct(limit: Int = 0): ProductDto {
        return client.get("/products") {
            parameter("limit", limit)
        }.body()
    }

}