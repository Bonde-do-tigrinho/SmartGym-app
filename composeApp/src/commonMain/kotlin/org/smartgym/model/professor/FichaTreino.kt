package org.smartgym.model.professor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FichaTreino(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("alunoId")
    val alunoId: Int,
    @SerialName("exercicios")
    val exercicios: List<ExercicioFichaTreino> = emptyList(),
    @SerialName("vigencia")
    val vigencia: String,
    @SerialName("focoTreino")
    val focoTreino: String,
    @SerialName("dataCriacao")
    val dataCriacao: String = ""
)

@Serializable
data class ExercicioFichaTreino(
    @SerialName("exercicioId")
    val exercicioId: Long,
    @SerialName("series")
    val series: Int,
    @SerialName("repeticoes")
    val repeticoes: Int,
    @SerialName("descansoSegundos")
    val descansoSegundos: Int
)


