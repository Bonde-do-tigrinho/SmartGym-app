package org.smartgym.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.smartgym.model.aluno.Agendamento
import org.smartgym.repository.ApiAlunoRepository
import org.smartgym.model.professor.AlunoResumido
import org.smartgym.repository.ApiAgendamentosRepository

// 🎯 DTO Local para estruturar a chamada da turma com o nome
data class ItemChamadaTurma(
    val agendamentoId: Int,
    val alunoId: Int,
    val alunoNome: String
)

class AgendamentosViewModel : ViewModel() {

    private val repository = ApiAgendamentosRepository()
    private val alunoRepository = ApiAlunoRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    private val _aulasAgendadasIds = MutableStateFlow<Set<Int>>(emptySet())
    val aulasAgendadasIds = _aulasAgendadasIds.asStateFlow()

    private val _alunosCadastrados = MutableStateFlow<List<AlunoResumido>>(emptyList())

    private val _agendamentosDaAula = MutableStateFlow<List<Agendamento>>(emptyList())
    val agendamentosDaAula = _agendamentosDaAula.asStateFlow()

    val listaDeChamadaComNomes: StateFlow<List<ItemChamadaTurma>> = combine(
        _agendamentosDaAula,
        _alunosCadastrados
    ) { agendamentos, alunos ->
        agendamentos.map { agendamento ->
            // Busca o nome do aluno na lista de cadastrados. Se não achar, usa um fallback elegante
            val nomeEncontrado = alunos.find { it.id == agendamento.alunoId }?.nome ?: "Aluno Matriculado"
            ItemChamadaTurma(
                agendamentoId = agendamento.id ?: 0,
                alunoId = agendamento.alunoId,
                alunoNome = nomeEncontrado
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun carregarAgendamentosDoAluno(alunoId: Int) {
        viewModelScope.launch {
            try {
                val agendamentos = repository.getAgendamentosDoAluno(alunoId)
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

    fun carregarAgendamentosDaAula(aulaId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Busca os agendamentos daquela aula específica primeiro
                val agendamentos = repository.getAgendamentosDaAula(aulaId)
                _agendamentosDaAula.value = agendamentos

                // 2. Busca todos os usuários do sistema
                try {
                    val respostaApi = alunoRepository.getAll()

                    // 🎯 O TRUQUE DO MAPEAMENTO:
                    // Forçamos o Kotlin a converter qualquer lista que venha do repositório (seja Usuario ou Aluno)
                    // em objetos limpos de AlunoResumido para o seu StateFlow reativo bater os tipos com sucesso.
                    val alunosMapeados = respostaApi.map { usuario ->
                        AlunoResumido(
                            id = usuario.id,    // Garanta que o tipo de ID aqui bate com o tipo do agendamento.alunoId (ex: Int)
                            nome = usuario.nome
                        )
                    }

                    _alunosCadastrados.value = alunosMapeados
                    println("LOG SMARTGYM -> Cruzamento realizado com sucesso para ${alunosMapeados.size} alunos.")

                } catch (err: Exception) {
                    println("Erro ao buscar ou converter lista de alunos: ${err.message}")
                }

            } catch (e: Exception) {
                println("Erro ao carregar lista de chamada: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limparListaDeChamada() {
        _agendamentosDaAula.value = emptyList()
        _alunosCadastrados.value = emptyList()
    }
}