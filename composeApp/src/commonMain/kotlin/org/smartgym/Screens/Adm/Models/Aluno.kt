package org.smartgym.Screens.Adm.Models

import androidx.compose.ui.graphics.Color

data class Aluno(
    val id: Int,
    val nome: String,
    val email: String,
    val telefone: String,
    val plano: String,
    val treinoAtual: String?,
    val focoTreino: String?,
    val planoVencimento: String?,
    val planoValor: Double?,
)