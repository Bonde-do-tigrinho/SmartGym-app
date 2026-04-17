package org.smartgym.repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.smartgym.model.professor.Exercicio
import org.smartgym.network.ApiClient

class ApiExercicioRepository {

    private val client = ApiClient.client

    private fun url(endpoint: String = "") = ApiClient.getUrl("/api/exercicios$endpoint")

    suspend fun getAll(): List<Exercicio> = client.get(url()).body()

    suspend fun getById(id: Long): Exercicio? = client.get(url("/$id")).body()

    suspend fun create(exercicio: Exercicio): Exercicio {
        return client.post(url()) {
            contentType(ContentType.Application.Json)
            setBody(exercicio)
        }.body()
    }

    suspend fun update(id: Long, exercicio: Exercicio): Exercicio {
        return client.put(url("/$id")) {
            contentType(ContentType.Application.Json)
            setBody(exercicio)
        }.body()
    }

    suspend fun delete(id: Long) = client.delete(url("/$id"))

    suspend fun getByNome(nome: String): List<Exercicio> =
        client.get(url("/search?nome=$nome")).body()

    suspend fun getByMaquina(maquinaId: Long): List<Exercicio> =
        client.get(url("/maquina/$maquinaId")).body()
}