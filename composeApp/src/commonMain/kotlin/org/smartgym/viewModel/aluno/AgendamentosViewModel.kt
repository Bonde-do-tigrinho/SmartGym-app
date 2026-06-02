package org.smartgym.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.smartgym.model.aluno.Agendamento
import org.smartgym.repository.ApiAgendamentosRepository

class AgendamentosViewModel : ViewModel() {

    private val repository = ApiAgendamentosRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    private val _aulasAgendadasIds = MutableStateFlow<Set<Int>>(emptySet())
    val aulasAgendadasIds = _aulasAgendadasIds.asStateFlow()

    fun carregarAgendamentosDoAluno(alunoId: Int) {
        viewModelScope.launch {
            try {
                val agendamentos = repository.getAgendamentosDoAluno(alunoId)
                // Extrai o ID da aula de cada agendamento e salva na nossa lista local
                _aulasAgendadasIds.value = agendamentos.map { it.aulaColetivaId }.toSet()
            } catch (e: Exception) {
                println("Erro ao carregar agendamentos do aluno: ${e.message}")
            }
        }
    }

    fun realizarAgendamento(alunoId: Int, aulaId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val agendamento = Agendamento(alunoId = alunoId, aulaColetivaId = aulaId)
                repository.agendar(agendamento)
                _aulasAgendadasIds.update { idsAtuais ->
                    idsAtuais.plus(aulaId)
                }
                _snackbarEvent.emit("Vaga garantida com sucesso!")
            } catch (e: Exception) {
                _snackbarEvent.emit("Não foi possível agendar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    // Estado para a tela do Professor (Lista de chamada)
    private val _agendamentosDaAula = MutableStateFlow<List<Agendamento>>(emptyList())
    val agendamentosDaAula = _agendamentosDaAula.asStateFlow()

    fun carregarAgendamentosDaAula(aulaId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _agendamentosDaAula.value = repository.getAgendamentosDaAula(aulaId)
            } catch (e: Exception) {
                println("Erro ao carregar lista de chamada: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun limparListaDeChamada() {
        _agendamentosDaAula.value = emptyList()
    }
}