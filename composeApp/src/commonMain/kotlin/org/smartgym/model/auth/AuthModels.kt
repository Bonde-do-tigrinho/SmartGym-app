package org.smartgym.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val senha: String
)

@Serializable
data class LoginResponse(
    val papel: String? = null,
    val role: String? = null,
    val nome: String? = null,
    val token: String? = null,
    val message: String? = null,
    val mensagem: String? = null
)

