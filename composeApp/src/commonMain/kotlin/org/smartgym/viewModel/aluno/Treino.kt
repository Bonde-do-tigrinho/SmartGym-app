package org.smartgym.viewModel.aluno

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import org.smartgym.model.aluno.Exercicio
import org.smartgym.model.aluno.TreinoDia

class TreinoViewModel : ViewModel() {

    // 1. O Banco de Dados Falso (Estado base)
    private val _treinos = MutableStateFlow(
        listOf(
            TreinoDia("A", "Peito\ne Tríc", listOf(
                Exercicio(1, "Supino Reto", 4, 10, "Peito", true),
                Exercicio(2, "Supino Inclinado", 3, 12, "Peito", true),
                Exercicio(3, "Crossover", 3, 15, "Peito", false),
                Exercicio(4, "Tríceps Pulley", 3, 12, "Tríceps", false),
                Exercicio(5, "Tríceps Testa", 4, 10, "Tríceps", false)
            )),
            TreinoDia("B", "Costas\ne Bíceps", listOf(
                Exercicio(6, "Puxada Frontal", 4, 12, "Costas", false),
                Exercicio(7, "Remada Curvada", 4, 10, "Costas", false)
            )),
            TreinoDia("C", "Pernas\ne Glúteos", listOf(
                Exercicio(8, "Leg Press", 4, 12, "Pernas", false),
                Exercicio(9, "Cadeira Extensora", 4, 15, "Pernas", false)
            ))
        )
    )
    val treinos = _treinos.asStateFlow()

    // 2. Qual treino está selecionado no momento? (Padrão: A)
    private val _diaSelecionado = MutableStateFlow("A")
    val diaSelecionado = _diaSelecionado.asStateFlow()

    // 3. Uma lista derivada: pega apenas os exercícios do dia selecionado
    val exerciciosDoDia = combine(_treinos, _diaSelecionado) { listaTreinos, dia ->
        listaTreinos.find { it.id == dia }?.exercicios ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 4. Ações disparadas pela View
    fun selecionarDia(idDia: String) {
        _diaSelecionado.value = idDia
    }

    fun alternarConclusaoExercicio(exercicioId: Int) {
        // Encontra o treino e inverte o status do exercício selecionado
        _treinos.value = _treinos.value.map { treino ->
            if (treino.id == _diaSelecionado.value) {
                treino.copy(exercicios = treino.exercicios.map { ex ->
                    if (ex.id == exercicioId) ex.copy(concluido = !ex.concluido) else ex
                })
            } else {
                treino
            }
        }
    }
}