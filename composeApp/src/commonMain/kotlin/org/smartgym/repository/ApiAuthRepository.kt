package org.smartgym.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.smartgym.auth.AuthResult
import org.smartgym.auth.TokenManager
import org.smartgym.model.auth.LoginRequest
import org.smartgym.model.auth.LoginResponse
import org.smartgym.network.ApiClient

class ApiAuthRepository {

    private val client = ApiClient.client
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    // Tenta múltiplos endpoints comuns de autenticação
    private val endpointsPossiveis = listOf(
        "/api/auth/login",
        "/auth/login",
        "/api/login",
        "/login"
    )

    suspend fun login(email: String, senha: String): AuthResult {
        val request = LoginRequest(email = email, senha = senha)
        var lastError: Exception? = null

        for (endpoint in endpointsPossiveis) {
            try {
                val response = client.post(ApiClient.getUrl(endpoint)) {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }

                val statusCode = response.status.value
                val bodyText = response.bodyAsText()

                if (statusCode == 200 || statusCode == 201) {
                    val loginResponse = runCatching {
                        json.decodeFromString<LoginResponse>(bodyText)
                    }.getOrNull()

                    // 🔐 Salva o token se disponível
                    loginResponse?.token?.let { TokenManager.setToken(it) }

                    val papel = loginResponse?.papel
                        ?: loginResponse?.role
                        ?: extrairPapelDoToken(loginResponse?.token)

                    if (papel != null) {
                        return AuthResult(
                            sucesso = true,
                            mensagem = loginResponse?.nome?.let { "Bem-vindo, $it!" } ?: "Login realizado com sucesso!",
                            papel = papel.lowercase()
                        )
                    }

                    // Se não veio papel mas veio 200, assume aluno como fallback
                    return AuthResult(
                        sucesso = true,
                        mensagem = "Login realizado com sucesso!",
                        papel = "aluno"
                    )
                }

                if (statusCode == 401 || statusCode == 403) {
                    return AuthResult(
                        sucesso = false,
                        mensagem = "Email ou senha incorretos",
                        papel = null
                    )
                }

            } catch (e: ResponseException) {
                val status = e.response.status.value
                if (status == 401 || status == 403) {
                    return AuthResult(
                        sucesso = false,
                        mensagem = "Email ou senha incorretos",
                        papel = null
                    )
                }
                lastError = e
            } catch (e: Exception) {
                lastError = e
            }
        }

        return AuthResult(
            sucesso = false,
            mensagem = "Não foi possível conectar ao servidor. Verifique sua conexão.",
            papel = null
        )
    }

    /**
     * Extrai o papel/role de um JWT token (sem biblioteca externa).
     * O payload do JWT é a segunda parte separada por ".".
     */
    private fun extrairPapelDoToken(token: String?): String? {
        if (token.isNullOrBlank()) return null
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null
            val payloadBase64 = parts[1]
                .replace('-', '+')
                .replace('_', '/')
                .padEnd((payloadBase64Len(parts[1])), '=')
            val decoded = decodeBase64(payloadBase64)
            val payloadJson = json.parseToJsonElement(decoded)
            payloadJson.jsonObject["papel"]?.jsonPrimitive?.content
                ?: payloadJson.jsonObject["role"]?.jsonPrimitive?.content
                ?: payloadJson.jsonObject["authorities"]?.jsonArray?.firstOrNull()
                    ?.jsonPrimitive?.content
                    ?.removePrefix("ROLE_")
                    ?.lowercase()
        } catch (e: Exception) {
            null
        }
    }

    private fun payloadBase64Len(s: String): Int {
        val remainder = s.length % 4
        return if (remainder == 0) s.length else s.length + (4 - remainder)
    }

    private fun decodeBase64(s: String): String {
        // Implementação básica de Base64 sem dependências externas
        val table = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        val bytes = mutableListOf<Byte>()
        var buffer = 0
        var bitsLeft = 0
        for (c in s) {
            if (c == '=') break
            val idx = table.indexOf(c)
            if (idx < 0) continue
            buffer = (buffer shl 6) or idx
            bitsLeft += 6
            if (bitsLeft >= 8) {
                bitsLeft -= 8
                bytes.add(((buffer shr bitsLeft) and 0xFF).toByte())
            }
        }
        return bytes.toByteArray().decodeToString()
    }
}


