package org.smartgym.repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.smartgym.model.Adm.Plano
import org.smartgym.network.ApiClient

class ApiPlanoRepository {
    private val client = ApiClient.client

    private fun url(path: String = "") = ApiClient.getUrl("/api/planos$path")

    suspend fun buscarTodos(): List<Plano> = client.get(url()).body()

    suspend fun salvar(plano: Plano) {
        client.post(url()) {
            contentType(ContentType.Application.Json)
            setBody(plano)
        }
    }

    suspend fun deletar(id: Int) {
        client.delete(url("/$id"))
    }
}