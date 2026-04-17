package org.smartgym.model.professor

import kotlinx.serialization.Serializable

@Serializable
data class Avaliacao(
    val id: Long? = null,
    val nomeAluno: String,
    val dataAvaliacao: String,
    val peso: String,
    val percentualGordura: String,
    val imc: String,
    val nota: String
)

