package org.smartgym.model.aluno

import kotlinx.serialization.Serializable

@Serializable
data class Agendamento(
    val id: Long? = null,
    val alunoId: Long = 0L,
    val aulaColetivaId: Long = 0L,
    val dataAgendamento: String? = null
)