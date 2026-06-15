package org.smartgym.repository
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import org.smartgym.model.professor.FichaTreino
import org.smartgym.network.ApiClient
import org.smartgym.util.formatDateToBackend
class ApiFichaTreinoRepository : FichaTreinoRepository {
    private val client = ApiClient.client
    private val basePaths = listOf("/api/fichas-treino")
    private fun url(basePath: String, path: String = "") = ApiClient.getUrl("$basePath$path")
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private fun normalizarVigencia(data: String): String = formatDateToBackend(data)

    private suspend fun <T> execute(request: suspend (String) -> T): T {
        var lastError: Exception? = null
        for (basePath in basePaths) {
            try {
                return request(basePath)
            } catch (e: ResponseException) {
                val body = runCatching { e.response.bodyAsText() }.getOrNull().orEmpty()
                if (e.response.status.value != 404 || basePath == basePaths.last()) {
                    val detalheBody = if (body.isNotBlank()) " | body=$body" else ""
                    throw IllegalStateException(
                        "Erro HTTP ${e.response.status.value} ao acessar ${url(basePath)}$detalheBody",
                        e
                    )
                }
                lastError = e
            } catch (e: Exception) {
                lastError = e
                if (basePath == basePaths.last()) {
                    throw IllegalStateException(
                        "Nao foi possivel concluir a operacao de fichas em ${url(basePath)}: ${e.message}",
                        e
                    )
                }
            }
        }
        throw IllegalStateException("Nao foi possivel concluir a operacao de fichas.", lastError)
    }
    override suspend fun getAll(): List<FichaTreino> = execute { basePath ->
        val response = client.get(url(basePath))
        val bodyText = response.bodyAsText()
        json.decodeFromString<List<FichaTreino>>(bodyText)
    }
    override suspend fun getById(id: Int): FichaTreino? = execute { basePath ->
        client.get(url(basePath, "/$id")).body()
    }
    override suspend fun getByAlunoId(alunoId: Int): List<FichaTreino> = execute { basePath ->
        client.get(url(basePath)) {
            url {
                parameters.append("alunoId", alunoId.toString())
            }
        }.body()
    }
    override suspend fun create(ficha: FichaTreino): FichaTreino? = execute { basePath ->

        // 💡 CORREÇÃO AQUI: Deixe o parâmetro em branco ou apenas "/"
        val urlFinal = url("/api/fichas-treino")

        println("=== [DEBUG FRONT-END] URL Final RECORRIGIDA: $urlFinal")

        val response = client.post(urlFinal) {
            contentType(ContentType.Application.Json)
            setBody(ficha)
        }

        if (response.status.isSuccess()) {
            response.body<FichaTreino>()
        } else {
            println("🚨 Erro de API no Front: Status ${response.status}")
            null
        }
    }

    override suspend fun update(id: Int, fichaTreino: FichaTreino): FichaTreino = execute { basePath ->
        val payload = fichaTreino

        println("=== INSPEÇÃO DE PAYLOAD (PUT) ===")
        println("ID da Ficha: $id")
        println("Aluno ID enviado: ${payload.alunoId}")
        println("Vigência enviada: '${payload.vigencia}'")
        println("Quantidade de Dias de Rotina: ${payload.rotinaDias.size}")
        payload.rotinaDias.forEach { dia ->
            println(" -> Treino ${dia.letra} | Foco: '${dia.focoTreino}' | Exercícios: ${dia.exercicios.size}")
            dia.exercicios.forEachIndexed { index, ex ->
                println("    [Exercicio $index] ID: ${ex.exercicioId} | Séries: ${ex.series} | Reps: ${ex.repeticoes} | Descanso: ${ex.descansoSegundos}s")
            }
        }
        println("=================================")

        val response = client.put(url(basePath, "/$id")) {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }

        if (response.status.value !in 200..299) {
            // 💡 Captura e exibe a mensagem de erro detalhada que o Spring possa ter devolvido no corpo
            val erroCorpo = try { response.body<String>() } catch(_: Exception) { "Corpo vazio ou ilegível" }
            println("🚨 DETALHE DO ERRO 400 DO SERVIDOR: $erroCorpo")

            throw Exception("Erro ao atualizar ficha no servidor: Status ${response.status.value}")
        }

        payload
    }

    override suspend fun delete(id: Int) {
        execute { basePath ->
            client.delete(url(basePath, "/$id"))
        }
    }

    override suspend fun getMinhaFicha(): FichaTreino? = execute { basePath ->
        val response = client.get(url(basePath, "/me"))
        if (response.status.value == 404) return@execute null
        response.body()
    }
}
