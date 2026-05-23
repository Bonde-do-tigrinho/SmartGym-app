package org.smartgym.model.auth


import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val senha: String
)

@Serializable
data class LoginResponse(
    val token: String? = null,
    val papel: String? = null,
    val role: String? = null,
    val nome: String? = null,
    val message: String? = null,
    val mensagem: String? = null
)

@Serializable
data class RegisterRequest(
    val nome: String,
    val email: String,
    val telefone: String,
    val senha: String
)

@Serializable
data class AuthResponse(
    val sucesso: Boolean,
    val mensagem: String
)

@Serializable
data class RecuperarSenhaRequest(
    val email: String
)
