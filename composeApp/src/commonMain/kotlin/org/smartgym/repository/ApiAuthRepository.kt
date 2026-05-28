package org.smartgym.repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.smartgym.Auth.TokenManager
import org.smartgym.model.auth.*
import org.smartgym.network.ApiClient

data class LoginResult(
    val sucesso: Boolean,
    val mensagem: String,
    val papel: String? = null,
    val perfilCompleto: Boolean = false
)

data class AuthResult(
    val sucesso: Boolean,
    val mensagem: String?
)

data class ResetarSenhaRequest(
    val token: String,
    val novaSenha: String
)

class ApiAuthRepository {

    suspend fun login(email: String, senha: String): LoginResult {
        return try {
            val response: LoginResponse = ApiClient.client.post(ApiClient.getUrl("/api/auth/login")) {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, senha))
            }.body()

            if (response.token != null) {
                TokenManager.setToken(response.token)
                LoginResult(
                    sucesso = true,
                    mensagem = "Login realizado com sucesso!",
                    papel = response.papel ?: response.role,
                    perfilCompleto = response.perfilCompleto
                )
            } else {
                LoginResult(
                    sucesso = false,
                    mensagem = response.mensagem ?: response.message ?: "Credenciais inválidas"
                )
            }
        } catch (e: Exception) {
            LoginResult(sucesso = false, mensagem = e.message ?: "Erro de conexão")
        }
    }

    suspend fun registrar(
        nome: String,
        email: String,
        cpf: String,
        telefone: String,
        senha: String
    ): AuthResult {
        return try {
            val response: AuthResponse = ApiClient.client.post(ApiClient.getUrl("/api/auth/register")) {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(nome, email, cpf, telefone, senha))
            }.body()

            val deuCerto = response.id != null || response.sucesso == true

            AuthResult(
                sucesso = deuCerto,
                mensagem = if (deuCerto) "Cadastro realizado! Verifique seu email." else (response.mensagem ?: "Erro no cadastro")
            )
        } catch (e: Exception) {
            AuthResult(sucesso = false, mensagem = e.message ?: "Erro de conexão")
        }
    }

    suspend fun recuperarSenha(email: String): AuthResult {
        return try {
            val response: AuthResponse = ApiClient.client.post(ApiClient.getUrl("/api/auth/recuperar-senha")) {
                contentType(ContentType.Application.Json)
                setBody(RecuperarSenhaRequest(email))
            }.body()
            AuthResult(sucesso = response.sucesso, mensagem = response.mensagem)
        } catch (e: Exception) {
            AuthResult(sucesso = false, mensagem = e.message ?: "Erro de conexão")
        }
    }

    suspend fun resetarSenha(token: String, novaSenha: String): AuthResult {
        return try {
            val response: AuthResponse = ApiClient.client.post(ApiClient.getUrl("/api/auth/resetar-senha")) {
                contentType(ContentType.Application.Json)
                setBody(ResetarSenhaRequest(token, novaSenha))
            }.body()
            AuthResult(sucesso = response.sucesso, mensagem = response.mensagem)
        } catch (e: Exception) {
            AuthResult(sucesso = false, mensagem = e.message ?: "Erro de conexão")
        }
    }
}