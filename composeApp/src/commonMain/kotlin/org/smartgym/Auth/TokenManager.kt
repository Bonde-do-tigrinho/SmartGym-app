package org.smartgym.Auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object TokenManager {

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    fun setToken(newToken: String?) {
        _token.value = newToken
        _isAuthenticated.value = !newToken.isNullOrBlank()
    }
    fun getToken(): String? = _token.value

    fun hasValidToken(): Boolean = !_token.value.isNullOrBlank()

    fun clearToken() {
        _token.value = null
        _isAuthenticated.value = false
    }

    fun getAuthorizationHeader(): String? {
        return _token.value?.let { "Bearer $it" }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun getUserId(): Int? {
        val currentToken = _token.value ?: return null
        return try {
            val parts = currentToken.split(".")
            if (parts.size < 2) return null

            val payloadBytes = Base64.UrlSafe.decode(parts[1])
            val payloadString = payloadBytes.decodeToString()

            val jsonObject = Json.parseToJsonElement(payloadString).jsonObject

            val idPrimitive = jsonObject["id"]?.jsonPrimitive ?: jsonObject["sub"]?.jsonPrimitive

            idPrimitive?.content?.toIntOrNull()
        } catch (e: Exception) {
            println("🚨 Erro ao decodificar ID do JWT: ${e.message}")
            null
        }
    }
}