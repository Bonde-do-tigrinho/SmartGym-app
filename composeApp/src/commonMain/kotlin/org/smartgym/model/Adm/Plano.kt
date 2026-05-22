package org.smartgym.model.Adm

import kotlinx.serialization.Serializable

@Serializable
data class Plano(
    val id: Int? = null,
    val nome: String,
    val descricao: String,
    val valor: Double,
    val duracaoMeses: Int,
    val ativo: Boolean
)