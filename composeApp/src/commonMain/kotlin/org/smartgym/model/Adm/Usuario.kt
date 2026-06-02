package org.smartgym.model.Adm

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: Int? = null,
    val nome: String,
    val email: String,
    val cpf: String,
    val telefone: String,
    val role: String? = null,
    val plano: Plano? = null,
    @EncodeDefault val status: Boolean = true,
    val treinoAtual: String? = null,
    val focoTreino: String? = null,
    val planoVencimento: String? = null,
    val planoValor: Double? = null
)