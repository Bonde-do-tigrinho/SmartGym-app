package org.smartgym.repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import kotlinx.serialization.json.Json
import org.smartgym.model.Adm.MaquinaIot
import org.smartgym.network.ApiClient

class ApiMaquinasIotRepository {
    private val client = ApiClient.client
    private fun url(endpoint: String = "") = ApiClient.getUrl("/api/maquinas-iot$endpoint")

    suspend fun getAll(): List<MaquinaIot> = client.get(url()).body()

    suspend fun getById(id: String): MaquinaIot? = client.get(url("/$id")).body()

    suspend fun create(maquinaIot: MaquinaIot): MaquinaIot {
        return client.post(url()) {
            contentType(ContentType.Application.Json)
            setBody(maquinaIot)
        }.body()
    }

    suspend fun update(id: String, maquinaIot: MaquinaIot): MaquinaIot {
        return client.put(url("/$id")) {
            contentType(ContentType.Application.Json)
            setBody(maquinaIot)
        }.body()
    }

    suspend fun delete(id: String) = client.delete(url("/$id"))

    suspend fun getByNome(nome: String): List<MaquinaIot> =
        client.get(url()) {
            parameter("nome", nome)
        }.body()

    suspend fun getAllBruto(): String = client.get(url()).body()

    suspend fun createRaw(maquinaIot: MaquinaIot): HttpResponse {
        // 🎯 1. Transforma o objeto MaquinaIot em uma String JSON idêntica à que vai na rede
        val jsonBruto = Json.encodeToString(MaquinaIot.serializer(), maquinaIot)

        // 🚨 O LOG DA VERDADE: Vai mostrar o texto exato do JSON no seu Logcat
        println("🔥 [KTOR JSON RAW STRING]: $jsonBruto")

        return client.post(url()) {
            contentType(ContentType.Application.Json)
            setBody(maquinaIot)
        }
    }

}