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

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    fun carregarPlanos() {
        viewModelScope.launch {
            try {
                _planos.value = repository.buscarTodos()
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao carregar: ${e.message}")
            }
        }
    }

    fun salvarPlano(plano: Plano) {
        viewModelScope.launch {
            try {
                repository.salvar(plano)
                _snackbarEvent.emit("Plano salvo com sucesso!")
                carregarPlanos()
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao salvar: ${e.message}")
            }
        }
    }

    fun deletarPlano(id: Int) {
        viewModelScope.launch {
            try {
                repository.deletar(id)
                _snackbarEvent.emit("Plano apagado.")
                carregarPlanos()
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao deletar: ${e.message}")
            }
        }
    }
}