package org.smartgym.viewModel.Adm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.smartgym.model.Adm.Aluno

class AlunosViewModel : ViewModel() {

    private val _alunos = MutableStateFlow<List<Aluno>>(emptyList())
    val alunos: StateFlow<List<Aluno>> = _alunos.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val alunosFiltrados: StateFlow<List<Aluno>> = combine(_alunos, _searchQuery) { lista, query ->
        if (query.isBlank()) lista
        else lista.filter { it.nome.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), _alunos.value)

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun adicionarAluno(
        nome: String,
        email: String,
        telefone: String,
        cpf: String,
        plano: String,
        status: Boolean
    ) {
        val novoAluno = Aluno(
            id = (_alunos.value.maxOfOrNull { it.id } ?: 0) + 1,
            nome = nome,
            email = email,
            telefone = telefone,
            cpf = cpf,
            plano = plano,
            status = status,
            treinoAtual = null,
            focoTreino = null,
            planoVencimento = null,
            planoValor = null
        )
        _alunos.value += novoAluno
    }

    fun deletarAluno(id: Int) {
        _alunos.value = _alunos.value.filter { it.id != id }
    }

    fun editarAluno(aluno: Aluno) {
        _alunos.value = _alunos.value.map { if (it.id == aluno.id) aluno else it }
    }
}