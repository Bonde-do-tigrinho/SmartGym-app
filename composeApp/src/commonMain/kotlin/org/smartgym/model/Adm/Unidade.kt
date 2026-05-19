package org.smartgym.model.Adm

import kotlinx.serialization.Serializable

@Serializable
data class Unidade(
    val id: Int? = null,
    val nome: String,
    val endereco: String,
    val cidade: String,
    val alunos: Int = 0,
    val instrutores: Int = 0
)