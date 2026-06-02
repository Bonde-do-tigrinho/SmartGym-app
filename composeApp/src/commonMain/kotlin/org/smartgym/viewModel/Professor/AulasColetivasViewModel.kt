package org.smartgym.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.smartgym.model.professor.AulaColetiva
import org.smartgym.model.aluno.AulasDoDia
import org.smartgym.repository.ApiAulasColetivasRepository
import kotlin.time.Clock

class AulasColetivasViewModel : ViewModel() {

    private val repository = ApiAulasColetivasRepository()

    private val _aulasDaSemana = MutableStateFlow<List<AulasDoDia>>(emptyList())
    val aulasDaSemana = _aulasDaSemana.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    private var dataBaseAtual: String = Clock.System.now().toString().substringBefore("T")

    fun carregarVisaoSemanal(novaDataIso: String? = null) {
        if (novaDataIso != null) {
            dataBaseAtual = novaDataIso
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resposta = repository.getAulasDaSemana(dataBaseAtual)
                _aulasDaSemana.value = resposta

                if (dataBaseAtual.isEmpty() && resposta.isNotEmpty()) {
                    dataBaseAtual = resposta[0].data
                }
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao carregar agenda: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun buscarAulaPorId(id: Int, onResult: (AulaColetiva) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val aula = repository.getById(id)
                onResult(aula)
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao buscar detalhes da aula: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun criarAula(aula: AulaColetiva, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.create(aula)
                carregarVisaoSemanal()
                _snackbarEvent.emit("Aula criada com sucesso!")
                onSuccess()
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao criar aula: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun atualizarAula(id: Int, aula: AulaColetiva, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.update(id, aula)
                carregarVisaoSemanal()
                _snackbarEvent.emit("Aula atualizada com sucesso!")
                onSuccess()
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao atualizar aula: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletarAula(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.delete(id)
                carregarVisaoSemanal()
                _snackbarEvent.emit("Aula excluída com sucesso!")
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao excluir: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}