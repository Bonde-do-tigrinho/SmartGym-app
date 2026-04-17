package org.smartgym.repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.smartgym.model.Adm.Maquina

import org.smartgym.network.ApiClient

class ApiMaquinasRepository {
    private val client = ApiClient.client
    private fun url(endpoint: String = "") = ApiClient.getUrl("/api/maquinas$endpoint")

    suspend fun getAll(): List<Maquina> = client.get(url()).body()

    suspend fun getById(id: Long): Maquina? = client.get(url("/$id")).body()

    suspend fun create(maquina: Maquina): Maquina {
        return client.post(url()) {
            contentType(ContentType.Application.Json)
            setBody(maquina)
        }.body()
    }

    suspend fun update(id: Long, maquina: Maquina): Maquina {
        return client.put(url("/$id")) {
            contentType(ContentType.Application.Json)
            setBody(maquina)
        }.body()
    }

    suspend fun delete(id: Long) = client.delete(url("/$id"))

    // O Ktor adiciona automaticamente o "?nome=Valor" no final da URL para bater com o seu @RequestParam
    suspend fun getByNome(nome: String): List<Maquina> =
        client.get(url()) {
            parameter("nome", nome)
        }.body()

}