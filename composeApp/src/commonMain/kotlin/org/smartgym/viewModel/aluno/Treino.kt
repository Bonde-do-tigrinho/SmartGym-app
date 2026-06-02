package org.smartgym.viewModel.aluno

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.smartgym.model.professor.Exercicio
import org.smartgym.model.professor.FichaTreino
import org.smartgym.model.professor.ExercicioFichaTreino
import org.smartgym.repository.ApiExercicioRepository
import org.smartgym.repository.FichaTreinoRepository

class TreinoViewModel(
    private val repository: FichaTreinoRepository,
    private val exercicioRepository: ApiExercicioRepository
) : ViewModel() {

    private val _fichaAtiva = MutableStateFlow<FichaTreino?>(null)
    val fichaAtiva = _fichaAtiva.asStateFlow()

    private val _listaExerciciosCadastrados = MutableStateFlow<List<Exercicio>>(emptyList())
    val listaExerciciosCadastrados = _listaExerciciosCadastrados.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _exerciciosConcluidosIds = MutableStateFlow<Set<Int>>(emptySet())
    val exerciciosConcluidosIds = _exerciciosConcluidosIds.asStateFlow()
    
    private val _letraSelecionada = MutableStateFlow("A") // Padrão começa no Treino A
    val letraSelecionada = _letraSelecionada.asStateFlow()


    val exerciciosDoDiaAtivo = combine(_fichaAtiva, _letraSelecionada) { ficha, letra ->
        ficha?.rotinaDias?.find { it.letra.equals(letra, ignoreCase = true) }?.exercicios ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val focoDoDiaAtivo = combine(_fichaAtiva, _letraSelecionada) { ficha, letra ->
        ficha?.rotinaDias?.find { it.letra.equals(letra, ignoreCase = true) }?.focoTreino ?: "Nenhum Foco"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Nenhum Foco")

    init {
        carregarMeuTreino()
    }

    fun carregarMeuTreino() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _listaExerciciosCadastrados.value = exercicioRepository.getAll()
                _fichaAtiva.value = repository.getMinhaFicha()

                _exerciciosConcluidosIds.value = emptySet()
            } catch (e: Exception) {
                _fichaAtiva.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selecionarDia(letra: String) {
        _letraSelecionada.value = letra
        _exerciciosConcluidosIds.value = emptySet()
    }

    fun obterNomeExercicio(id: Int): String {
        return _listaExerciciosCadastrados.value.find { it.id == id }?.nome ?: "Exercício #$id"
    }

    fun obterGrupoMuscular(id: Int): String {
        return _listaExerciciosCadastrados.value.find { it.id == id }?.grupoMuscular ?: "Geral"
    }

    fun alternarConclusaoExercicio(exercicioId: Int) {
        val atuais = _exerciciosConcluidosIds.value.toMutableSet()
        if (atuais.contains(exercicioId)) {
            atuais.remove(exercicioId)
        } else {
            atuais.add(exercicioId)
        }
        _exerciciosConcluidosIds.value = atuais
    }
}