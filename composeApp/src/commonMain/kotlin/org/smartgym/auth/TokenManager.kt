package org.smartgym.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gerencia o armazenamento e recuperação do token JWT
 * Usa uma abordagem em memória para Compose Multiplatform
 * Em produção, considere usar EncryptedSharedPreferences (Android) ou Keychain (iOS)
 */
object TokenManager {

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    /**
     * Salva o token JWT
     */
    fun setToken(newToken: String?) {
        _token.value = newToken
        _isAuthenticated.value = !newToken.isNullOrBlank()
    }

    /**
     * Recupera o token JWT atual
     */
    fun getToken(): String? = _token.value

    /**
     * Verifica se há token válido
     */
    fun hasValidToken(): Boolean = !_token.value.isNullOrBlank()

    /**
     * Limpa o token (logout)
     */
    fun clearToken() {
        _token.value = null
        _isAuthenticated.value = false
    }

    /**
     * Retorna o header Authorization completo
     */
    fun getAuthorizationHeader(): String? {
        return _token.value?.let { "Bearer $it" }
    }
}

