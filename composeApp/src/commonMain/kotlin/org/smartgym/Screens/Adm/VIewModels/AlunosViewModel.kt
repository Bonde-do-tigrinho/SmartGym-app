package org.smartgym.Screens.Adm.VIewModels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.smartgym.Screens.Adm.Models.Aluno

class AlunosViewModel : ViewModel() {

    private val _alunos = MutableStateFlow(listaInicial())
    val alunos: StateFlow<List<Aluno>> = _alunos.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val alunosFiltrados: StateFlow<List<Aluno>> = combine(_alunos, _searchQuery) { lista, query ->
        if (query.isBlank()) lista
        else lista.filter { it.nome.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listaInicial())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun listaInicial() = listOf(
        Aluno(1, "Lucas Mendes", "lucas@email.com", "(11) 98765-4321", "Premium", null, null, null, null ),
        Aluno(2, "Fernanda Lima", "fernanda@email.com", "(11) 98765-1234", "Basic", null, null, null, null ),
        Aluno(3, "João Silva", "joao@email.com", "(11) 98765-5678", "Black", null, null, null, null )
    )
}