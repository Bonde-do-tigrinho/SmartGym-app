package org.smartgym.model.Adm

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class StatusMaquinaIot {
    LIVRE,
    OCUPADA,
    MANUTENCAO
}

@Serializable
data class MaquinaIot(
    @EncodeDefault val id: String? = null,
    @SerialName("deviceId") val deviceId: String = "",
    val nome: String = "",
    val localizacao: String = "",
    val categoria: String = "Cardio",
    @EncodeDefault val status: StatusMaquinaIot = StatusMaquinaIot.LIVRE
)