package org.smartgym.repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import org.smartgym.model.professor.Exercicio
import org.smartgym.network.ApiClient

class ApiExercicioRepository {

    private val client = ApiClient.client
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private fun url(endpoint: String = "") = ApiClient.getUrl("/api/exercicios$endpoint")

    suspend fun getAll(): List<Exercicio> = client.get(url()).body()

    suspend fun getById(id: Long): Exercicio? = client.get(url("/$id")).body()

    suspend fun create(exercicio: Exercicio): Exercicio {
        val response = client.post(url()) {
            contentType(ContentType.Application.Json)
            setBody(exercicio)
        }
        return parseExercicioResponse(response, exercicio)
    }

    suspend fun update(id: Long, exercicio: Exercicio): Exercicio {
        val response = client.put(url("/$id")) {
            contentType(ContentType.Application.Json)
            setBody(exercicio)
        }
        return parseExercicioResponse(response, exercicio)
    }

    private suspend fun parseExercicioResponse(
        response: HttpResponse,
        fallback: Exercicio
    ): Exercicio {
        val bodyText = runCatching { response.bodyAsText() }.getOrNull()
        if (!bodyText.isNullOrBlank()) {
            runCatching { json.decodeFromString<Exercicio>(bodyText) }.getOrNull()?.let { return it }
        }
        return fallback
    }

    suspend fun delete(id: Long) = client.delete(url("/$id"))

    suspend fun getByNome(nome: String): List<Exercicio> {
        val termo = nome.trim()
        if (termo.isBlank()) return getAll()

        return getAll().filter { exercicio ->
            exercicio.nome.contains(termo, ignoreCase = true) ||
                exercicio.id?.toString() == termo
        }
    }

    suspend fun getByMaquina(maquinaId: Long): List<Exercicio> =
        client.get(url("/maquina/$maquinaId")).body()
}