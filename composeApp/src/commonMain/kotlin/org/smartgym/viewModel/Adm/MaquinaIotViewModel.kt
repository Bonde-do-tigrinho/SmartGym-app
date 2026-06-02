package org.smartgym.viewModel.Adm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.smartgym.model.Adm.MaquinaIot
import org.smartgym.repository.ApiMaquinasIotRepository

class MaquinaIotViewModel : ViewModel() {

    private val repository = ApiMaquinasIotRepository()

    private val _maquinasIot = MutableStateFlow<List<MaquinaIot>>(emptyList())
    val maquinasIot = _maquinasIot.asStateFlow()

    private val _maquinaIotSelecionada = MutableStateFlow<MaquinaIot?>(null)
    val maquinaIotSelecionada = _maquinaIotSelecionada.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    init {
        carregarMaquinasIot()
    }

    fun carregarMaquinasIot() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                try {
                    val jsonBruto = repository.getAllBruto()
                } catch (e: Exception) {
                }

                val lista = repository.getAll()
                _maquinasIot.value = emptyList()
                _maquinasIot.value = lista

            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao carregar máquinas IOT: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun buscarPorNome(nome: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (nome.isBlank()) {
                    _maquinasIot.value = repository.getAll()
                } else {
                    _maquinasIot.value = repository.getByNome(nome)
                }
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao buscar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun carregarPorId(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _maquinaIotSelecionada.value = repository.getById(id)
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao carregar detalhes: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun criarMaquinaIot(maquinaIot: MaquinaIot) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response: HttpResponse = repository.createRaw(maquinaIot)

                if (response.status.value in 200..299) {
                    _snackbarEvent.emit("Máquina IOT criada com sucesso!")
                    carregarMaquinasIot()
                } else {
                    _snackbarEvent.emit("Erro no Servidor: Status ${response.status.value}")
                }

            } catch (e: Exception) {
                println("🚨 [ERRO DE REDE/CONEXÃO]: ${e.message}")
                _snackbarEvent.emit("Erro ao conectar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun atualizarMaquinaIot(id: String, maquinaIot: MaquinaIot) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.update(id, maquinaIot)
                _snackbarEvent.emit("Máquina IOT actualizada com sucesso!")
                carregarMaquinasIot()
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao atualizar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletarMaquinaIot(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.delete(id)
                _snackbarEvent.emit("Máquina IOT excluída com sucesso!")
                carregarMaquinasIot()
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao excluir: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limparSelecao() {
        _maquinaIotSelecionada.value = null
    }
}