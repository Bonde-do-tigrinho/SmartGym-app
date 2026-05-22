package org.smartgym.repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.smartgym.model.Adm.Plano
import org.smartgym.network.ApiClient

class ApiPlanoRepository {
    private val client = ApiClient.client
    private fun url(endpoint: String = "") = ApiClient.getUrl("/api/planos$endpoint")

    suspend fun getAll(): List<Plano> = client.get(url()).body()

    suspend fun getById(id: Int): Plano? = client.get(url("/$id")).body()

    suspend fun create(plano: Plano): Plano {
        return client.post(url()) {
            contentType(ContentType.Application.Json)
            setBody(plano)
        }.body()
    }

    suspend fun update(id: Int, plano: Plano): Plano {
        return client.put(url("/$id")) {
            contentType(ContentType.Application.Json)
            setBody(plano)
        }.body()
    }

    suspend fun delete(id: Int) {
        client.delete(url("/$id"))
    }
}