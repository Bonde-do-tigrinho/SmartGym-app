package org.smartgym.model.professor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FichaTreino(
    @SerialName("id") val id: Int? = null,
    @SerialName("alunoId") val alunoId: Int,
    @SerialName("vigencia") val vigencia: String,
    @SerialName("rotinaDias") val rotinaDias: List<TreinoDia> = emptyList()
)

@Serializable
data class TreinoDia(
    @SerialName("id") val id: Int? = null,
    @SerialName("letra") val letra: String,
    @SerialName("focoTreino") val focoTreino: String,
    @SerialName("exercicios") val exercicios: List<ExercicioFichaTreino> = emptyList()
)

@Serializable
data class ExercicioFichaTreino(
    @SerialName("id") val id: Int? = null,
    @SerialName("exercicioId") val exercicioId: Int,
    @SerialName("series") val series: Int,
    @SerialName("repeticoes") val repeticoes: Int,
    @SerialName("descansoSegundos") val descansoSegundos: Int
)


