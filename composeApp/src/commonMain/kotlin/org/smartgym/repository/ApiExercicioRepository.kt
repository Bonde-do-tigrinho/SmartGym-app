package org.smartgym.repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.smartgym.model.professor.Exercicio

class ApiExercicioRepository(private val client: HttpClient) : ExercicioRepository {

    private val baseUrl = "http://10.0.2.2:8080/api/exercicios" // Coloque a URL da sua API

    override suspend fun getAll(): List<Exercicio> {
        return client.get(baseUrl).body()
    }

    override suspend fun getById(id: Long): Exercicio? {
        return client.get("$baseUrl/$id").body()
    }

    override suspend fun create(exercicio: Exercicio): Exercicio {
        return client.post(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(exercicio)
        }.body()
    }

    override suspend fun update(id: Long, exercicio: Exercicio): Exercicio {
        return client.put("$baseUrl/$id") {
            contentType(ContentType.Application.Json)
            setBody(exercicio)
        }.body()
    }

    override suspend fun delete(id: Long) {
        client.delete("$baseUrl/$id")
    }

    override suspend fun getByNome(nome: String): List<Exercicio> {
        return client.get("$baseUrl/search?nome=$nome").body()
    }

    override suspend fun getByMaquina(maquinaId: Long): List<Exercicio> {
        return client.get("$baseUrl/maquina/$maquinaId").body()
    }
}