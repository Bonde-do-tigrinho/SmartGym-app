package org.smartgym.model.Adm

import kotlinx.serialization.Serializable

@Serializable
data class Notificacao(
    val id: Int? = null,
    val titulo: String,
    val mensagem: String,
    val dataPostagem: String? = null,
    val dataExpiracao: String? = null,
    val categoria: String
)