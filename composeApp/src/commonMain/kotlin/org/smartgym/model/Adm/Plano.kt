package org.smartgym.model.Adm

import kotlinx.serialization.Serializable

@Serializable
data class Plano(
    val id: Int? = null,
    val nome: String,
    val ativo: Boolean,
    val dataFimPromocao: String,
    val horarioLimiteAcesso: String
)