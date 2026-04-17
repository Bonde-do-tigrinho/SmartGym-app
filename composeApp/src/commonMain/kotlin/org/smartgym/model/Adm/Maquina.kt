package org.smartgym.model.Adm

import kotlinx.serialization.Serializable

@Serializable
data class Maquina(
    val id: Int?,
    val nome: String,
    val localizacao: String,
    val status: String,
)