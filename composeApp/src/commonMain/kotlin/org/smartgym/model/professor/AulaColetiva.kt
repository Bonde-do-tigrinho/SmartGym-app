package org.smartgym.model.professor

import kotlinx.serialization.Serializable

@Serializable
data class AulaColetiva(
    val id: Int? = null,
    val nome: String,
    val professorId: Int,
    val capacidadeMaxima: Int,
    val dataHoraInicio: String,
    val dataHoraFim: String
)