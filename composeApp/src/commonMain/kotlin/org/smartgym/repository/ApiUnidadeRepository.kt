package org.smartgym.repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.smartgym.model.Adm.Unidade
import org.smartgym.network.ApiClient

class ApiUnidadeRepository {
    private val client = ApiClient.client

    // Aponta para o endpoint que criamos no Spring
    private fun url(path: String = "") = ApiClient.getUrl("/unidades$path")

    suspend fun buscarTodas(): List<Unidade> = client.get(url()).body()

    suspend fun salvar(unidade: Unidade) {
        if (unidade.id == null) {
            // POST - Criar novo
            client.post(url()) {
                contentType(ContentType.Application.Json)
                setBody(unidade)
            }
        } else {
            // PUT - Atualizar existente
            client.put(url("/${unidade.id}")) {
                contentType(ContentType.Application.Json)
                setBody(unidade)
            }
        }
    }

    suspend fun apagar(id: Int) {
        // DELETE
        client.delete(url("/$id"))
    }
}