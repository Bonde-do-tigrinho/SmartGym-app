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
import org.smartgym.util.formatDateToUi
import org.smartgym.util.isValidUiDate
import org.smartgym.util.maskDateInput

class AvaliacoesViewModel(
    private val repository: AvaliacaoRepository,
    private val alunoRepository: AlunoRepository
) : ViewModel() {

    // 🎯 Mantendo apenas o StateFlow central de avaliações (igual ao '_fichas' da Ficha)
    private val _avaliacoes = MutableStateFlow<List<Avaliacao>>(emptyList())
    val avaliacoes: StateFlow<List<Avaliacao>> = _avaliacoes.asStateFlow()

    private val _alunosResumo = MutableStateFlow<List<AlunoResumido>>(emptyList())
    val alunosResumo: StateFlow<List<AlunoResumido>> = _alunosResumo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent: SharedFlow<Unit> = _navigationEvent.asSharedFlow()

    // Estados do Formulário (Criação/Edição)
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

    private val _selectedAlunoId = MutableStateFlow<Int?>(null)
    val selectedAlunoId: StateFlow<Int?> = _selectedAlunoId.asStateFlow()

    init {
        loadAll()
        loadAlunosResumo()
    }

    fun updateSearchQuery(value: String) { _searchQuery.value = value }
    fun updateNomeAluno(value: String) { _nomeAluno.value = value }
    fun updateSelectedAlunoId(value: Int) {
        _selectedAlunoId.value = value
        _nomeAluno.value = _alunosResumo.value.firstOrNull { it.id == value }?.nome.orEmpty()
    }
    fun updateDataAvaliacao(value: String) { _dataAvaliacao.value = maskDateInput(value) }
    fun updatePeso(value: String) { _peso.value = value }
    fun updatePercentualGordura(value: String) { _percentualGordura.value = value }
    fun updateImc(value: String) { _imc.value = value }
    fun updateNota(value: String) { _nota.value = value }

    fun loadAll() {
        viewModelScope.launch {
            _isLoading.value = true
            println("⚙️ [VIEWMODEL] Chamando loadAll() via token...")
            try {
                // Chama a nova rota filtrada do Ktor
                val lista = repository.getAvaliacoesProfessor()
                _avaliacoes.value = lista.sortedByDescending { it.id ?: 0 }
                println("⚙️ [VIEWMODEL] StateFlow 'avaliacoes' atualizado com ${lista.size} itens.")
            } catch (e: Exception) {
                _avaliacoes.value = emptyList()
                println("🚨 [VIEWMODEL ERRO] Falha ao carregar avaliações: ${e.message}")
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
                        ?.nome ?: _nomeAluno.value
                }
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao carregar alunos: ${e.message}")
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
                    _dataAvaliacao.value = formatDateToUi(avaliacao.dataAvaliacao)
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

    fun save() {
        viewModelScope.launch {
            println("⚙️ [DEBUG] Botão salvar clicado! Validando formulário...")
            if (!formularioValido()) {
                println("🚨 [DEBUG] Formulário INVÁLIDO! Dados atuais: AlunoID=${_selectedAlunoId.value}, Data=${_dataAvaliacao.value}, Peso=${_peso.value}")
                _snackbarEvent.emit("Preencha todos os campos corretamente.")
                return@launch
            }
            println("📡 [DEBUG] Formulário VÁLIDO! Disparando Ktor create()...")
            _isLoading.value = true

            try {
                val idEdicao = _editingId.value

                val dataFormatadaParaApi = try {
                    val partes = _dataAvaliacao.value.trim().split("/")
                    "${partes[2]}-${partes[1]}-${partes[0]}"
                } catch (e: Exception) {
                    _dataAvaliacao.value.trim()
                }

                val avaliacao = Avaliacao(
                    id = idEdicao ?: 0,
                    alunoId = _selectedAlunoId.value ?: 0,
                    nomeAluno = _nomeAluno.value.trim(),
                    dataAvaliacao = dataFormatadaParaApi,
                    professorId = 0,
                    peso = _peso.value.trim().replace(",", ".").toDouble(),
                    percentualGordura = _percentualGordura.value.trim().replace(",", ".").toDouble(),
                    imc = _imc.value.trim().replace(",", ".").toDouble(),
                    nota = _nota.value.trim()
                )

                if (idEdicao == null) {
                    repository.create(avaliacao)
                    _snackbarEvent.emit("Avaliacao cadastrada com sucesso!")
                } else {
                    repository.update(idEdicao, avaliacao)
                    _snackbarEvent.emit("Avaliacao atualizada com sucesso!")
                }

                clearForm()
                loadAll()
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
                loadAll()
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
                isValidUiDate(_dataAvaliacao.value) &&
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

    fun filteredAvaliacoes(): List<Avaliacao> {
        val query = _searchQuery.value.trim()
        val base = _avaliacoes.value
        if (query.isBlank()) return base

        val alunoId = query.toIntOrNull()

        return base.filter { avaliacao ->
            avaliacao.nomeAluno.contains(query, ignoreCase = true) ||
                    avaliacao.nota.contains(query, ignoreCase = true) ||
                    avaliacao.id?.toString() == query ||
                    (alunoId != null && avaliacao.alunoId == alunoId)
        }
    }

    fun carregarAvaliacoes() {
        loadAll()
    }
}