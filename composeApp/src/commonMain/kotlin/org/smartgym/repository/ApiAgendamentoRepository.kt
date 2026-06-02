package org.smartgym.repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.smartgym.model.aluno.Agendamento
import org.smartgym.network.ApiClient
// 👇 NOVOS IMPORTS PARA LER O JSON DO ERRO
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ApiAgendamentosRepository {
    private val client = ApiClient.client
    private fun url(endpoint: String = "") = ApiClient.getUrl("/api/agendamentos$endpoint")

    suspend fun agendar(agendamento: Agendamento) {
        val response = client.post(url()) {
            contentType(ContentType.Application.Json)
            setBody(agendamento)
        }

        // Verifica se a API retornou algum erro (ex: 400 Bad Request, 500)
        if (response.status.value !in 200..299) {
            val erroRaw = response.bodyAsText()

            val mensagemAmigavel = try {
                val json = Json { ignoreUnknownKeys = true }
                val jsonObject = json.parseToJsonElement(erroRaw).jsonObject

                jsonObject["message"]?.jsonPrimitive?.content ?: "Recusado pelo servidor."
            } catch (e: Exception) {
                "Erro interno no servidor (${response.status.value})"
            }

            // Lança a exceção só com o texto que o usuário deve ler!
            throw Exception(mensagemAmigavel)
        }
    }

    suspend fun cancelar(id: Long) = client.delete(url("/$id"))

    suspend fun getAgendamentosDoAluno(alunoId: Int): List<Agendamento> =
        client.get(url("/aluno/$alunoId")).body()

    suspend fun getAgendamentosDaAula(aulaId: Int): List<Agendamento> =
        client.get(url("/aula/$aulaId")).body()
}