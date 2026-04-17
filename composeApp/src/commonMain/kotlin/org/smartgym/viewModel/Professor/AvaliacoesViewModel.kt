package org.smartgym.viewModel.Professor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.smartgym.model.professor.Avaliacao
import org.smartgym.repository.AvaliacaoRepository

class AvaliacoesViewModel(private val repository: AvaliacaoRepository) : ViewModel() {

    private val _avaliacoes = MutableStateFlow<List<Avaliacao>>(emptyList())
    val avaliacoes: StateFlow<List<Avaliacao>> = _avaliacoes.asStateFlow()

    private val _nomeAluno = MutableStateFlow("")
    val nomeAluno: StateFlow<String> = _nomeAluno.asStateFlow()

    private val _dataAvaliacao = MutableStateFlow("")
    val dataAvaliacao: StateFlow<String> = _dataAvaliacao.asStateFlow()

    private val _peso = MutableStateFlow("")
    val peso: StateFlow<String> = _peso.asStateFlow()

    private val _percentualGordura = MutableStateFlow("")
    val percentualGordura: StateFlow<String> = _percentualGordura.asStateFlow()

    private val _imc = MutableStateFlow("")
    val imc: StateFlow<String> = _imc.asStateFlow()

    private val _nota = MutableStateFlow("")
    val nota: StateFlow<String> = _nota.asStateFlow()

    private val _editingId = MutableStateFlow<Long?>(null)
    val editingId: StateFlow<Long?> = _editingId.asStateFlow()

    fun updateNomeAluno(value: String) { _nomeAluno.value = value }
    fun updateDataAvaliacao(value: String) { _dataAvaliacao.value = value }
    fun updatePeso(value: String) { _peso.value = value }
    fun updatePercentualGordura(value: String) { _percentualGordura.value = value }
    fun updateImc(value: String) { _imc.value = value }
    fun updateNota(value: String) { _nota.value = value }

    fun loadAll() {
        viewModelScope.launch {
            try {
                _avaliacoes.value = repository.getAll()
            } catch (e: Exception) {
                println("Erro ao carregar avaliacoes: ${e.message}")
                _avaliacoes.value = emptyList()
            }
        }
    }

    fun loadByNomeAluno(nomeAluno: String) {
        viewModelScope.launch {
            try {
                _avaliacoes.value = repository.getByNomeAluno(nomeAluno)
            } catch (e: Exception) {
                println("Erro ao buscar avaliacoes: ${e.message}")
            }
        }
    }

    fun loadById(id: Long) {
        viewModelScope.launch {
            try {
                repository.getById(id)?.let { avaliacao ->
                    _editingId.value = id
                    _nomeAluno.value = avaliacao.nomeAluno
                    _dataAvaliacao.value = avaliacao.dataAvaliacao
                    _peso.value = avaliacao.peso
                    _percentualGordura.value = avaliacao.percentualGordura
                    _imc.value = avaliacao.imc
                    _nota.value = avaliacao.nota
                }
            } catch (e: Exception) {
                println("Erro ao carregar avaliacao por ID: ${e.message}")
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            if (_editingId.value != null) {
                update(_editingId.value!!)
            } else {
                create()
            }
        }
    }

    private fun create() {
        viewModelScope.launch {
            try {
                val avaliacao = Avaliacao(
                    nomeAluno = _nomeAluno.value,
                    dataAvaliacao = _dataAvaliacao.value,
                    peso = _peso.value,
                    percentualGordura = _percentualGordura.value,
                    imc = _imc.value,
                    nota = _nota.value
                )
                repository.create(avaliacao)
                loadAll()
                clearForm()
            } catch (e: Exception) {
                println("Erro ao criar avaliacao: ${e.message}")
            }
        }
    }

    private fun update(id: Long) {
        viewModelScope.launch {
            try {
                val avaliacao = Avaliacao(
                    id = id,
                    nomeAluno = _nomeAluno.value,
                    dataAvaliacao = _dataAvaliacao.value,
                    peso = _peso.value,
                    percentualGordura = _percentualGordura.value,
                    imc = _imc.value,
                    nota = _nota.value
                )
                repository.update(id, avaliacao)
                loadAll()
                clearForm()
            } catch (e: Exception) {
                println("Erro ao atualizar avaliacao: ${e.message}")
            }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            try {
                repository.delete(id)
                loadAll()
            } catch (e: Exception) {
                println("Erro ao deletar avaliacao: ${e.message}")
            }
        }
    }

    fun clearForm() {
        _editingId.value = null
        _nomeAluno.value = ""
        _dataAvaliacao.value = ""
        _peso.value = ""
        _percentualGordura.value = ""
        _imc.value = ""
        _nota.value = ""
    }
}

