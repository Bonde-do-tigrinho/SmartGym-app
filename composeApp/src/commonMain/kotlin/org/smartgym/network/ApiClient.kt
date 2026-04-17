package org.smartgym.network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ApiClient {

    // Android Emulador → 10.0.2.2
    // Web/Desktop → localhost:8080
    //android fisico -> http://192.168.x.x:8080
    private const val BASE_URL = "http://10.0.2.2:8080"

    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    fun getUrl(endpoint: String) = "$BASE_URL$endpoint"
}