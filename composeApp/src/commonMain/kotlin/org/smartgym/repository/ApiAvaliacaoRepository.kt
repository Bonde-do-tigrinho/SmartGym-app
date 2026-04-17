package org.smartgym.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.smartgym.model.professor.Avaliacao

class ApiAvaliacaoRepository(private val client: HttpClient) : AvaliacaoRepository {

    private val timeoutPorHostMs = 5000L

    private val baseUrls = listOf(
        // Android fisico + USB debugging (com `adb reverse tcp:8080 tcp:8080`)
        "http://localhost:8080/api/avaliacoes",
        "http://127.0.0.1:8080/api/avaliacoes",
        // Android emulator
        "http://10.0.2.2:8080/api/avaliacoes"
    )

    private suspend fun <T> executeWithBaseUrl(request: suspend (String) -> T): T {
        var lastError: Exception? = null
        val falhas = mutableListOf<String>()

        for (baseUrl in baseUrls) {
            try {
                return withTimeout(timeoutPorHostMs) {
                    request(baseUrl)
                }
            } catch (e: ResponseException) {
                // Erro HTTP indica que conectou no backend; nao devemos mascarar como erro de conexao.
                val body = runCatching { e.response.bodyAsText() }.getOrNull().orEmpty()
                val detalheBody = if (body.isNotBlank()) " | body=$body" else ""
                throw IllegalStateException(
                    "Erro HTTP ${e.response.status.value} ao acessar $baseUrl$detalheBody",
                    e
                )
            } catch (e: TimeoutCancellationException) {
                falhas.add("$baseUrl => timeout apos ${timeoutPorHostMs}ms")
                lastError = IllegalStateException("timeout ao acessar $baseUrl", e)
            } catch (e: Exception) {
                falhas.add("$baseUrl => ${e.message ?: e::class.simpleName}")
                lastError = e
            }
        }

        val tentativas = baseUrls.joinToString()
        val detalhesFalhas = if (falhas.isNotEmpty()) " | falhas: ${falhas.joinToString("; ")}" else ""
        val causa = lastError?.message?.let { " | causa final: $it" } ?: ""
        val dica =
            "Nao foi possivel conectar ao backend de avaliacoes. Hosts tentados: [$tentativas]. " +
            "Se estiver em telefone fisico via USB, execute: adb reverse tcp:8080 tcp:8080 " +
            "e confirme que o backend esta ativo em localhost:8080.$detalhesFalhas$causa"

        throw IllegalStateException(dica, lastError)
    }

    private fun normalizarData(data: String): String {
        val valor = data.trim()
        val partes = valor.split("/")

        // Converte dd/MM/yyyy para yyyy-MM-dd para backend com LocalDate.
        return if (partes.size == 3 && partes[0].length == 2 && partes[1].length == 2 && partes[2].length == 4) {
            "${partes[2]}-${partes[1]}-${partes[0]}"
        } else {
            valor
        }
    }

    override suspend fun getAll(): List<Avaliacao> {
        return executeWithBaseUrl { baseUrl ->
            client.get(baseUrl).body()
        }
    }

    override suspend fun getById(id: Long): Avaliacao? {
        return executeWithBaseUrl { baseUrl ->
            client.get("$baseUrl/$id").body()
        }
    }

    override suspend fun create(avaliacao: Avaliacao): Avaliacao {
        val payload = avaliacao.copy(dataAvaliacao = normalizarData(avaliacao.dataAvaliacao))
        return executeWithBaseUrl { baseUrl ->
            client.post(baseUrl) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }.body()
        }
    }

    override suspend fun update(id: Long, avaliacao: Avaliacao): Avaliacao {
        val payload = avaliacao.copy(dataAvaliacao = normalizarData(avaliacao.dataAvaliacao))
        return executeWithBaseUrl { baseUrl ->
            client.put("$baseUrl/$id") {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }.body()
        }
    }

    override suspend fun delete(id: Long) {
        executeWithBaseUrl { baseUrl ->
            client.delete("$baseUrl/$id")
        }
    }

    override suspend fun getByNomeAluno(nomeAluno: String): List<Avaliacao> {
        val termo = nomeAluno.trim()
        if (termo.isBlank()) return getAll()

        fun filtrarLocalmente(lista: List<Avaliacao>): List<Avaliacao> {
            return lista.filter { it.nomeAluno.contains(termo, ignoreCase = true) }
        }

        return try {
            val porNomeAluno: List<Avaliacao> = executeWithBaseUrl { baseUrl ->
                client.get("$baseUrl/search") {
                    parameter("nomeAluno", termo)
                }.body()
            }

            if (porNomeAluno.isNotEmpty()) {
                porNomeAluno
            } else {
                val porNome: List<Avaliacao> = executeWithBaseUrl { baseUrl ->
                    client.get("$baseUrl/search") {
                        parameter("nome", termo)
                    }.body()
                }

                if (porNome.isNotEmpty()) porNome else filtrarLocalmente(getAll())
            }
        } catch (_: Exception) {
            // Fallback para manter a busca funcional mesmo com contrato de endpoint diferente.
            filtrarLocalmente(getAll())
        }
    }
}

