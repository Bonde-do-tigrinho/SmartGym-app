package org.smartgym.repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.smartgym.model.Adm.Notificacao
import org.smartgym.network.ApiClient

class ApiNotificacaoRepository {
    private val client = ApiClient.client

    // Aponta exatamente para o NotificacaoController do Spring Boot
    private fun url(path: String = "") = ApiClient.getUrl("/api/notificacoes$path")

    suspend fun buscarTodas(): List<Notificacao> = client.get(url()).body()

    suspend fun salvar(notificacao: Notificacao) {
        if (notificacao.id == null) {
            // POST - Criar novo aviso
            client.post(url()) {
                contentType(ContentType.Application.Json)
                setBody(notificacao)
            }
        } else {
            // PUT - Atualizar aviso existente
            client.put(url("/${notificacao.id}")) {
                contentType(ContentType.Application.Json)
                setBody(notificacao)
            }
        }
    }

    suspend fun apagar(id: Int) {
        // DELETE - Apgar aviso
        client.delete(url("/$id"))
    }
}