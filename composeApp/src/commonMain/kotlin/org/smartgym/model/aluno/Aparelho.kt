package org.smartgym.model.aluno

data class Aparelho(
    val id: Int,
    val name: String,
    val category: String,
    val timeRemaining: Int?, // null se estiver disponível
    val isOnline: Boolean = true
)
