package org.smartgym.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import org.smartgym.model.professor.Avaliacao
import org.smartgym.network.ApiClient

class ApiAvaliacaoRepository : AvaliacaoRepository {

    private val client = ApiClient.client
    private val basePaths = listOf("/api/avaliacoes", "/api/avaliacao", "/avaliacoes", "/avaliacao")

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private fun url(basePath: String, path: String = "") = ApiClient.getUrl("$basePath$path")

    private suspend fun <T> execute(request: suspend (String) -> T): T {
        var lastError: Exception? = null

        for (basePath in basePaths) {
            try {
                return request(basePath)
            } catch (e: ResponseException) {
                val body = runCatching { e.response.bodyAsText() }.getOrNull().orEmpty()
                if (e.response.status.value != 404 || basePath == basePaths.last()) {
                    val detalheBody = if (body.isNotBlank()) " | body=$body" else ""
                    throw IllegalStateException(
                        "Erro HTTP ${e.response.status.value} ao acessar ${url(basePath)}$detalheBody",
                        e
                    )
                }
                lastError = e
            } catch (e: Exception) {
                lastError = e
                if (basePath == basePaths.last()) {
                    throw IllegalStateException(
                        "Nao foi possivel concluir a operacao de avaliacoes em ${url(basePath)}: ${e.message}",
                        e
                    )
                }
            }
        }

        throw IllegalStateException(
            "Nao foi possivel concluir a operacao de avaliacoes.",
            lastError
        )
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
        return execute { basePath ->
            val response = client.get(url(basePath))
            val jsonBody = response.bodyAsText()
            println("DEBUG - GET $basePath Response: $jsonBody")
            val avaliacoes = json.decodeFromString<List<Avaliacao>>(jsonBody)
            println("DEBUG - Deserialized avaliacoes: $avaliacoes")
            avaliacoes
        }
    }

    override suspend fun getById(id: Int): Avaliacao? {
        return execute { basePath ->
            client.get(url(basePath, "/$id")).body()
        }
    }

    override suspend fun create(avaliacao: Avaliacao) {
        val payload = avaliacao.copy(dataAvaliacao = normalizarData(avaliacao.dataAvaliacao))
        execute { basePath ->
            client.post(url(basePath)) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
        }
    }

    override suspend fun update(id: Int, avaliacao: Avaliacao) {
        val payload = avaliacao.copy(dataAvaliacao = normalizarData(avaliacao.dataAvaliacao))
        execute { basePath ->
            client.put(url(basePath, "/$id")) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
        }
    }

    override suspend fun delete(id: Int) {
        execute { basePath ->
            client.delete(url(basePath, "/$id"))
        }
    }

    override suspend fun getByNomeAluno(nomeAluno: String): List<Avaliacao> {
        val termo = nomeAluno.trim()
        if (termo.isBlank()) return getAll()

        return getAll().filter { avaliacao ->
            avaliacao.nomeAluno.contains(termo, ignoreCase = true) ||
                avaliacao.id.toString() == termo
        }
    }
}

