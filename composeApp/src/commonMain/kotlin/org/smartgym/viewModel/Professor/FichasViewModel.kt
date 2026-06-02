package org.smartgym.viewModel.Professor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.smartgym.model.professor.AlunoResumido
import org.smartgym.model.professor.FichaTreino
import org.smartgym.repository.AlunoRepository
import org.smartgym.repository.FichaTreinoRepository

class FichasViewModel(
    private val repository: FichaTreinoRepository,
    private val alunoRepository: AlunoRepository
) : ViewModel() {
    private val _fichas = MutableStateFlow<List<FichaTreino>>(emptyList())
    val fichas: StateFlow<List<FichaTreino>> = _fichas.asStateFlow()

    private val _alunos = MutableStateFlow<List<AlunoResumido>>(emptyList())
    val alunos: StateFlow<List<AlunoResumido>> = _alunos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    init {
        loadAll()
        loadAlunos()
    }

    fun updateSearchQuery(value: String) {
        _searchQuery.value = value
    }

    fun loadAll() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _fichas.value = repository.getAll().sortedByDescending { it.id ?: 0 }
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao carregar fichas")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadByAlunoId(alunoId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _fichas.value = repository.getByAlunoId(alunoId).sortedByDescending { it.id ?: 0 }
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao filtrar fichas")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.delete(id)
                loadAll()
                _snackbarEvent.emit("Ficha removida")
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao excluir ficha")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAlunos() {
        viewModelScope.launch {
            try {
                _alunos.value = alunoRepository.getAll()
            } catch (_: Exception) {
                _alunos.value = emptyList()
            }
        }
    }

    fun alunoNome(alunoId: Int): String {
        return _alunos.value.firstOrNull { it.id == alunoId }?.nome ?: "Aluno #$alunoId"
    }

    fun filteredFichas(): List<FichaTreino> {
        val query = _searchQuery.value.trim()
        val base = _fichas.value
        if (query.isBlank()) return base

        val alunoId = query.toIntOrNull()

        return base.filter { ficha ->
            val todosFocosDoTreino = ficha.rotinaDias.joinToString(" ") { it.focoTreino }

            todosFocosDoTreino.contains(query, ignoreCase = true) ||
                    alunoNome(ficha.alunoId).contains(query, ignoreCase = true) ||
                    ficha.id?.toString() == query ||
                    (alunoId != null && ficha.alunoId == alunoId)
        }
    }
}