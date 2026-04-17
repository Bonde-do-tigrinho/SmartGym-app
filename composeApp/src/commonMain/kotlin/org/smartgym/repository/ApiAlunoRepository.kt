package org.smartgym.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import org.smartgym.model.professor.AlunoResumido
import org.smartgym.network.ApiClient

class ApiAlunoRepository : AlunoRepository {

    private val client = ApiClient.client
    private val basePaths = listOf("/api/alunos", "/alunos", "/api/aluno", "/aluno")

    override suspend fun getAll(): List<AlunoResumido> {
        var lastError: Exception? = null

        for (basePath in basePaths) {
            try {
                return client.get(ApiClient.getUrl(basePath)).body()
            } catch (e: ResponseException) {
                if (e.response.status.value != 404 || basePath == basePaths.last()) {
                    val body = runCatching { e.response.bodyAsText() }.getOrNull().orEmpty()
                    val detalheBody = if (body.isNotBlank()) " | body=$body" else ""
                    throw IllegalStateException(
                        "Erro HTTP ${e.response.status.value} ao acessar ${ApiClient.getUrl(basePath)}$detalheBody",
                        e
                    )
                }
                lastError = e
            } catch (e: Exception) {
                lastError = e
                if (basePath == basePaths.last()) {
                    throw IllegalStateException(
                        "Nao foi possivel carregar alunos em ${ApiClient.getUrl(basePath)}: ${e.message}",
                        e
                    )
                }
            }
        }

        throw IllegalStateException("Nao foi possivel carregar alunos.", lastError)
    }
}

