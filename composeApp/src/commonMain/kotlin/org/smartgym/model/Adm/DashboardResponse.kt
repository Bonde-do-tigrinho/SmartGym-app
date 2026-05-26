package org.smartgym.model.Adm

import kotlinx.serialization.Serializable

@Serializable
data class DashboardResponse(
    val totalAlunos: Int,
    val totalProfessores: Int,
    val totalUnidades: Int,
    val alunosAtivos: Int,
    val alunosInativos: Int
)