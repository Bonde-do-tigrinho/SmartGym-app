package org.smartgym.model.professor

import kotlinx.serialization.Serializable

@Serializable
enum class TipoExercicio { LIVRE, MAQUINA, AEROBICO }

@Serializable
data class Exercicio(
    val id: Int? = null,
    val nome: String,
    val descricao: String,
    val tipo: TipoExercicio,
    val grupoMuscular: String? = null,
    val maquinaId: Int? = null
)
