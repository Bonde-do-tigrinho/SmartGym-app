package org.smartgym.model.aluno

import kotlinx.serialization.Serializable
import org.smartgym.model.professor.AulaColetiva

@Serializable
data class AulasDoDia(
    val data: String,
    val aulas: List<AulaColetiva>
)