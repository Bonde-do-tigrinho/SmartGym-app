package org.smartgym.model.professor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Avaliacao(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("alunoId")
    val alunoId: Int = 0,
    @SerialName("nomeAluno")
    val nomeAluno: String,
    @SerialName("dataAvaliacao")
    val dataAvaliacao: String,
    @SerialName("peso")
    val peso: Double,
    @SerialName("percentualGordura")
    val percentualGordura: Double,
    @SerialName("imc")
    val imc: Double,
    @SerialName("nota")
    val nota: String
)
