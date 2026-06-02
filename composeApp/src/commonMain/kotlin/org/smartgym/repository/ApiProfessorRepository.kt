package org.smartgym.repository

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import org.smartgym.model.Adm.Usuario
import org.smartgym.model.professor.AlunoResumido
import org.smartgym.network.ApiClient

class ApiProfessorRepository {

    private val client = ApiClient.client

    private fun url(endpoint: String = "") = ApiClient.getUrl("/api/professores$endpoint")

    suspend fun getAll(): List<Usuario> = client.get(url()).body()

    suspend fun getById(id: Int): Usuario? = client.get(url("/$id")).body()

    suspend fun create(usuario: Usuario): Usuario {
        return client.post(url()) {
            contentType(ContentType.Application.Json)
            setBody(usuario)
        }.body()
    }

    suspend fun update(id: Int, usuario: Usuario): Usuario {
        return client.put(url("/$id")) {
            contentType(ContentType.Application.Json)
            setBody(usuario)
        }.body()
    }

    suspend fun delete(id: Int) {
        client.delete(url("/$id"))
    }

    suspend fun getMeusAlunos(): List<AlunoResumido> {
        return try {

            val urlCompleta = ApiClient.getUrl("/api/usuarios/meus-alunos")

            val response = client.get(urlCompleta) {
                contentType(ContentType.Application.Json)
            }

            if (response.status.isSuccess()) {
                response.body<List<AlunoResumido>>()
            } else {
                println("🚨 Erro na API ao buscar meus alunos: Status ${response.status}")
                emptyList()
            }
        } catch (e: Exception) {
            println("🚨 Exceção ao buscar meus alunos no Ktor: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}