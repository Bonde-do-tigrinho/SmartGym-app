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
import org.smartgym.util.formatDateToBackend

class ApiAvaliacaoRepository : AvaliacaoRepository {

    private val client = ApiClient.client
    private val basePaths = listOf("/api/avaliacoes")

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
        val payload = avaliacao.copy(dataAvaliacao = formatDateToBackend(avaliacao.dataAvaliacao))
        execute { basePath ->
            client.post(url(basePath)) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
        }
    }

    override suspend fun update(id: Int, avaliacao: Avaliacao): Unit = execute { basePath ->
        println("📡 [KTOR PUT] Atualizando avaliação ID: $id no endpoint: ${url(basePath, "/$id")}")

        val response = client.put(url(basePath, "/$id")) {
            contentType(ContentType.Application.Json)
            setBody(avaliacao)
        }

        if (response.status.value !in 200..299) {
            val erroCorpo = runCatching { response.bodyAsText() }.getOrNull().orEmpty()
            println("🚨 [KTOR PUT ERRO] Status ${response.status.value} | Body: $erroCorpo")
            throw Exception("Erro ao atualizar avaliação no servidor.")
        }

        avaliacao
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

    override suspend fun getAvaliacoesProfessor(): List<Avaliacao> = execute { basePath ->
        val urlFinal = url(basePath, "/professor")

        val response = client.get(urlFinal)
        val bodyText = response.bodyAsText()

        if (bodyText.isBlank() || bodyText == "[]") {
            emptyList()
        } else {
            try {
                val listaMapeada = json.decodeFromString<List<Avaliacao>>(bodyText)
                listaMapeada
            } catch (e: Exception) {
                throw e
            }
        }
    }
}

