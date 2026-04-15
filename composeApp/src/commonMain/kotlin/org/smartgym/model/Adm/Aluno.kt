package org.smartgym.model.Adm

data class Aluno(
    val id: Int,
    val nome: String,
    val email: String,
    val telefone: String,
    val cpf: String,
    val plano: String,
    val status: Boolean,
    val treinoAtual: String?,
    val focoTreino: String?,
    val planoVencimento: String?,
    val planoValor: Double?,
)