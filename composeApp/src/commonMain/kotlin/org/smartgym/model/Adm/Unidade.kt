package org.smartgym.model.Adm

data class Unidade(
    val id: String,
    val nome: String,
    val endereco: String,
    val cidade: String,
    val alunos: Int = 0,
    val instrutores: Int = 0
)