package org.smartgym.repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.smartgym.model.professor.AulaColetiva
import org.smartgym.model.aluno.AulasDoDia
import org.smartgym.network.ApiClient

class ApiAulasColetivasRepository {
    private val client = ApiClient.client
    private fun url(endpoint: String = "") = ApiClient.getUrl("/api/aulas-coletivas$endpoint")

    private suspend fun checarErro(response: HttpResponse) {
        if (response.status.value !in 200..299) {
            val erroRaw = response.bodyAsText()
            val mensagemAmigavel = try {
                val json = Json { ignoreUnknownKeys = true }
                val jsonObject = json.parseToJsonElement(erroRaw).jsonObject
                jsonObject["message"]?.jsonPrimitive?.content ?: "Erro no servidor (${response.status.value})"
            } catch (e: Exception) {
                "Erro interno no servidor (${response.status.value})"
            }
            throw Exception(mensagemAmigavel)
        }
    }

    suspend fun create(aula: AulaColetiva): AulaColetiva {
        val response = client.post(url()) {
            contentType(ContentType.Application.Json)
            setBody(aula)
        }
        checarErro(response)
        return response.body()
    }

    suspend fun update(id: Int, aula: AulaColetiva): AulaColetiva {
        val response = client.put(url("/$id")) {
            contentType(ContentType.Application.Json)
            setBody(aula)
        }
        checarErro(response)
        return response.body()
    }

    suspend fun delete(id: Int) {
        val response = client.delete(url("/$id"))
        checarErro(response)
    }

    suspend fun getAulasDaSemana(dataInicio: String): List<AulasDoDia> {
        val response = client.get(url("/semana")) {
            parameter("dataInicio", dataInicio)
        }
        checarErro(response)
        return response.body()
    }

    suspend fun getById(id: Int): AulaColetiva {
        val response = client.get(url("/$id"))
        checarErro(response)
        return response.body()
    }
}