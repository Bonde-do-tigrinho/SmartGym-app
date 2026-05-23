package org.smartgym.Auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
}