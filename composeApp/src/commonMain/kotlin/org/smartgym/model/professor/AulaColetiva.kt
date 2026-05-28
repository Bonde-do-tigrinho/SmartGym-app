package org.smartgym.model.professor

import kotlinx.serialization.Serializable

@Serializable
data class AulaColetiva(
    val id: Long? = null,
    val nome: String,
    val professorId: Long,
    val capacidadeMaxima: Int,
    val dataHoraInicio: String,
    val dataHoraFim: String
)