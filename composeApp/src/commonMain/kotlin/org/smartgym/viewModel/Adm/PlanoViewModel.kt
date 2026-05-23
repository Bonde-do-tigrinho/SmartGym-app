package org.smartgym.viewModel.Adm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.smartgym.model.Adm.Plano
import org.smartgym.repository.ApiPlanoRepository

class PlanoViewModel : ViewModel() {
    private val repository = ApiPlanoRepository()

    private val _planos = MutableStateFlow<List<Plano>>(emptyList())
    val planos = _planos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    init {
        carregarPlanos()
    }

    fun carregarPlanos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _planos.value = repository.getAll()
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao carregar planos: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun criarPlano(plano: Plano) {
        viewModelScope.launch {
            try {
                repository.create(plano)
                _snackbarEvent.emit("Plano criado com sucesso!")
                carregarPlanos() // Atualiza a lista
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao criar plano: ${e.message}")
            }
        }
    }

    fun atualizarPlano(id: Int, plano: Plano) {
        viewModelScope.launch {
            try {
                repository.update(id, plano)
                _snackbarEvent.emit("Plano atualizado com sucesso!")
                carregarPlanos() // Atualiza a lista
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao atualizar plano: ${e.message}")
            }
        }
    }

    fun deletarPlano(id: Int) {
        viewModelScope.launch {
            try {
                repository.delete(id)
                _snackbarEvent.emit("Plano excluído com sucesso.")
                carregarPlanos() // Atualiza a lista
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao excluir plano: ${e.message}")
            }
        }
    }
}