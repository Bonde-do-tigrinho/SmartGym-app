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
import org.smartgym.model.professor.Avaliacao
import org.smartgym.repository.AlunoRepository
import org.smartgym.repository.AvaliacaoRepository

class AvaliacoesViewModel(
    private val repository: AvaliacaoRepository,
    private val alunoRepository: AlunoRepository
) : ViewModel() {

    private val _avaliacoes = MutableStateFlow<List<Avaliacao>>(emptyList())
    val avaliacoes: StateFlow<List<Avaliacao>> = _avaliacoes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent: SharedFlow<Unit> = _navigationEvent.asSharedFlow()

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

    private val _editingId = MutableStateFlow<Int?>(null)
    val editingId: StateFlow<Int?> = _editingId.asStateFlow()

    private val _alunosResumo = MutableStateFlow<List<AlunoResumido>>(emptyList())
    val alunosResumo: StateFlow<List<AlunoResumido>> = _alunosResumo.asStateFlow()

    private val _selectedAlunoId = MutableStateFlow<Int?>(null)
    val selectedAlunoId: StateFlow<Int?> = _selectedAlunoId.asStateFlow()

    init {
        loadAlunosResumo()
    }

    fun updateNomeAluno(value: String) { _nomeAluno.value = value }
    fun updateSelectedAlunoId(value: Int) {
        _selectedAlunoId.value = value
        _nomeAluno.value = _alunosResumo.value.firstOrNull { it.id == value }?.nome.orEmpty()
    }
    fun updateDataAvaliacao(value: String) { _dataAvaliacao.value = value }
    fun updatePeso(value: String) { _peso.value = value }
    fun updatePercentualGordura(value: String) { _percentualGordura.value = value }
    fun updateImc(value: String) { _imc.value = value }
    fun updateNota(value: String) { _nota.value = value }

    fun loadAll() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _avaliacoes.value = repository.getAll()
            } catch (e: Exception) {
                _avaliacoes.value = emptyList()
                _snackbarEvent.emit("Erro ao carregar avaliacoes: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadByNomeAluno(nomeAluno: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _avaliacoes.value = repository.getByNomeAluno(nomeAluno)
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao buscar avaliacoes: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getById(id)?.let { avaliacao ->
                    _editingId.value = id
                    _selectedAlunoId.value = avaliacao.alunoId.takeIf { it > 0 }
                    _nomeAluno.value = avaliacao.nomeAluno
                    _dataAvaliacao.value = avaliacao.dataAvaliacao
                    _peso.value = avaliacao.peso.toString()
                    _percentualGordura.value = avaliacao.percentualGordura.toString()
                    _imc.value = avaliacao.imc.toString()
                    _nota.value = avaliacao.nota
                }
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao carregar avaliacao: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAlunosResumo() {
        viewModelScope.launch {
            try {
                _alunosResumo.value = alunoRepository.getAll().sortedBy { it.nome.lowercase() }

                val selecionadoAtual = _selectedAlunoId.value
                if (selecionadoAtual != null) {
                    _nomeAluno.value = _alunosResumo.value
                        .firstOrNull { it.id == selecionadoAtual }
                        ?.nome
                        ?: _nomeAluno.value
                }
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao carregar alunos: ${e.message}")
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            if (!formularioValido()) {
                _snackbarEvent.emit("Preencha todos os campos. Peso, % Gordura e IMC devem ser numeros validos.")
                return@launch
            }

            _isLoading.value = true
            try {
                val avaliacao = Avaliacao(
                    id = _editingId.value ?: 0,
                    alunoId = _selectedAlunoId.value ?: 0,
                    nomeAluno = _nomeAluno.value.trim(),
                    dataAvaliacao = _dataAvaliacao.value.trim(),
                    peso = _peso.value.trim().replace(",", ".").toDouble(),
                    percentualGordura = _percentualGordura.value.trim().replace(",", ".").toDouble(),
                    imc = _imc.value.trim().replace(",", ".").toDouble(),
                    nota = _nota.value.trim()
                )

                val idEdicao = _editingId.value
                if (idEdicao == null) {
                    repository.create(avaliacao)
                    _snackbarEvent.emit("Avaliacao cadastrada com sucesso!")
                } else {
                    repository.update(idEdicao, avaliacao)
                    _snackbarEvent.emit("Avaliacao atualizada com sucesso!")
                }

                clearForm()
                _navigationEvent.emit(Unit)
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao salvar avaliacao: ${e.message}")
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
                _avaliacoes.value = repository.getAll()
                _snackbarEvent.emit("Avaliacao removida com sucesso!")
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao deletar avaliacao: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun formularioValido(): Boolean {
        return (_selectedAlunoId.value ?: 0) > 0 &&
            _nomeAluno.value.isNotBlank() &&
            _dataAvaliacao.value.isNotBlank() &&
            _peso.value.isNotBlank() && _peso.value.replace(",", ".").toDoubleOrNull() != null &&
            _percentualGordura.value.isNotBlank() && _percentualGordura.value.replace(",", ".").toDoubleOrNull() != null &&
            _imc.value.isNotBlank() && _imc.value.replace(",", ".").toDoubleOrNull() != null &&
            _nota.value.isNotBlank()
    }

    fun clearForm() {
        _editingId.value = null
        _selectedAlunoId.value = null
        _nomeAluno.value = ""
        _dataAvaliacao.value = ""
        _peso.value = ""
        _percentualGordura.value = ""
        _imc.value = ""
        _nota.value = ""
    }
}

