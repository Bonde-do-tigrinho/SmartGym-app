package org.smartgym.model.aluno

import kotlinx.serialization.Serializable

@Serializable
data class Agendamento(
    val id: Int? = null,
    val alunoId: Int = 0,
    val aulaColetivaId: Int = 0,
    val dataAgendamento: String? = null
)