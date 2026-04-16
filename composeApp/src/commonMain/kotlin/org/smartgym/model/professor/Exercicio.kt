package org.smartgym.model.professor

import kotlinx.serialization.Serializable

@Serializable
enum class TipoExercicio { LIVRE, MAQUINA }

@Serializable
data class Exercicio(
    val id: Long? = null,
    val nome: String,
    val descricao: String,
    val tipo: TipoExercicio = TipoExercicio.LIVRE,
    val maquinaId: Long? = null
)
