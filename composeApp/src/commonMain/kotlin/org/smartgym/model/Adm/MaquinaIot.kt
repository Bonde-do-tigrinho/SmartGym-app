package org.smartgym.model.Adm

import kotlinx.serialization.Serializable

@Serializable
data class MaquinaIot(
    val id: String?,
    val nome: String,
    val localizacao: String,
    val status: String,
)