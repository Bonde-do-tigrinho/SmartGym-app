package org.smartgym.network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import org.smartgym.auth.TokenManager

// 🔐 Plugin personalizado para adicionar Authorization header automaticamente
class AuthorizationHeaderPlugin {
    companion object Plugin : HttpClientPlugin<Unit, AuthorizationHeaderPlugin> {
        override val key = AttributeKey<AuthorizationHeaderPlugin>("AuthorizationHeaderPlugin")

        override fun prepare(block: Unit.() -> Unit) = AuthorizationHeaderPlugin()

        override fun install(plugin: AuthorizationHeaderPlugin, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Before) { content ->
                val authHeader = TokenManager.getAuthorizationHeader()
                if (authHeader != null) {
                    context.header("Authorization", authHeader)
                }
            }
        }
    }
}

object ApiClient {

    // Android Emulador → 10.0.2.2
    // Web/Desktop → localhost:8080
    //android fisico -> http://192.168.x.x:8080
    private const val BASE_URL = "http://localhost:8080"

    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

        // 🔐 Instala o plugin de autorização
        install(AuthorizationHeaderPlugin)
    }

    fun getUrl(endpoint: String) = "$BASE_URL$endpoint"
}
