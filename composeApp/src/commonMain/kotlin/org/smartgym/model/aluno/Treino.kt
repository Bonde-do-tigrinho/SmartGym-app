package org.smartgym.model.aluno

data class Exercicio(
    val id: Int,
    val nome: String,
    val series: Int,
    val repeticoes: Int,
    val grupoMuscular: String, // ex: "Peito"
    val concluido: Boolean = false
)

data class TreinoDia(
    val id: String,      // "A", "B" ou "C"
    val titulo: String,  // "Peito e Tríc"
    val exercicios: List<Exercicio>
)