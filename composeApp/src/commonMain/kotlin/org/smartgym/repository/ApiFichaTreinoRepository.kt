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
import kotlinx.serialization.json.Json
import org.smartgym.model.professor.FichaTreino
import org.smartgym.network.ApiClient
import org.smartgym.util.formatDateToBackend
class ApiFichaTreinoRepository : FichaTreinoRepository {
    private val client = ApiClient.client
    private val basePaths = listOf("/api/fichas-treino", "/api/ficha-treino", "/fichas-treino", "/ficha-treino")
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
        println("DEBUG FichaTreino getAll: $bodyText")
        json.decodeFromString<List<FichaTreino>>(bodyText)
    }
    override suspend fun getById(id: Long): FichaTreino? = execute { basePath ->
        client.get(url(basePath, "/$id")).body()
    }
    override suspend fun getByAlunoId(alunoId: Int): List<FichaTreino> = execute { basePath ->
        client.get(url(basePath)) {
            url {
                parameters.append("alunoId", alunoId.toString())
            }
        }.body()
    }
    override suspend fun create(fichaTreino: FichaTreino): FichaTreino = execute { basePath ->
        val payload = fichaTreino.copy(vigencia = normalizarVigencia(fichaTreino.vigencia))
        client.post(url(basePath)) {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }.body()
    }
    override suspend fun update(id: Long, fichaTreino: FichaTreino): FichaTreino = execute { basePath ->
        val payload = fichaTreino.copy(vigencia = normalizarVigencia(fichaTreino.vigencia))
        client.put(url(basePath, "/$id")) {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }.body()
    }
    override suspend fun delete(id: Long) {
        execute { basePath ->
            client.delete(url(basePath, "/$id"))
        }
    }
}
