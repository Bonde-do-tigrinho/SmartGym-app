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
import org.smartgym.model.professor.Exercicio
import org.smartgym.model.professor.ExercicioFichaTreino
import org.smartgym.model.professor.FichaTreino
import org.smartgym.repository.AlunoRepository
import org.smartgym.repository.ApiExercicioRepository
import org.smartgym.repository.FichaTreinoRepository
import org.smartgym.util.formatDateToUi

class CriarFichaViewModel(
    private val alunoRepository: AlunoRepository,
    private val exercicioRepository: ApiExercicioRepository,
    private val fichaRepository: FichaTreinoRepository
) : ViewModel() {

    private val _alunos = MutableStateFlow<List<AlunoResumido>>(emptyList())
    val alunos: StateFlow<List<AlunoResumido>> = _alunos.asStateFlow()

    private val _exercicios = MutableStateFlow<List<Exercicio>>(emptyList())
    val exercicios: StateFlow<List<Exercicio>> = _exercicios.asStateFlow()

    private val _selectedAlunoId = MutableStateFlow<Int?>(null)
    val selectedAlunoId: StateFlow<Int?> = _selectedAlunoId.asStateFlow()

    private val _nomeAluno = MutableStateFlow("")
    val nomeAluno: StateFlow<String> = _nomeAluno.asStateFlow()

    private val _focoTreino = MutableStateFlow("")
    val focoTreino: StateFlow<String> = _focoTreino.asStateFlow()

    private val _vigencia = MutableStateFlow("")
    val vigencia: StateFlow<String> = _vigencia.asStateFlow()

    private val _selectedExercicios = MutableStateFlow<List<ExercicioFichaTreino>>(emptyList())
    val selectedExercicios: StateFlow<List<ExercicioFichaTreino>> = _selectedExercicios.asStateFlow()

    private val _editingId = MutableStateFlow<Long?>(null)
    val editingId: StateFlow<Long?> = _editingId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent: SharedFlow<Unit> = _navigationEvent.asSharedFlow()

    fun loadInitialData(force: Boolean = false) {
        if (!force && _alunos.value.isNotEmpty() && _exercicios.value.isNotEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                _alunos.value = alunoRepository.getAll().sortedBy { it.nome.lowercase() }
                _exercicios.value = exercicioRepository.getAll().sortedBy { it.nome.lowercase() }

                val alunoSelecionado = _selectedAlunoId.value
                if (alunoSelecionado != null) {
                    _nomeAluno.value = _alunos.value.firstOrNull { it.id == alunoSelecionado }?.nome.orEmpty()
                }
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao carregar dados")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadById(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                fichaRepository.getById(id)?.let { ficha ->
                    _editingId.value = ficha.id
                    _selectedAlunoId.value = ficha.alunoId
                    _nomeAluno.value = _alunos.value.firstOrNull { it.id == ficha.alunoId }?.nome.orEmpty()
                    _focoTreino.value = ficha.focoTreino
                    _vigencia.value = formatDateToUi(ficha.vigencia)
                    _selectedExercicios.value = ficha.exercicios
                }
            } catch (_: Exception) {
                _snackbarEvent.emit("Erro ao carregar ficha")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun prepareForEdit(id: Long) {
        _editingId.value = id
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _alunos.value = alunoRepository.getAll().sortedBy { it.nome.lowercase() }
                _exercicios.value = exercicioRepository.getAll().sortedBy { it.nome.lowercase() }

                fichaRepository.getById(id)?.let { ficha ->
                    _editingId.value = ficha.id
                    _selectedAlunoId.value = ficha.alunoId
                    _nomeAluno.value = _alunos.value.firstOrNull { it.id == ficha.alunoId }?.nome.orEmpty()
                    _focoTreino.value = ficha.focoTreino
                    _vigencia.value = formatDateToUi(ficha.vigencia)
                    _selectedExercicios.value = ficha.exercicios
                }
            } catch (_: Exception) {
                _snackbarEvent.emit("Erro ao carregar ficha")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSelectedAluno(aluno: AlunoResumido) {
        _selectedAlunoId.value = aluno.id
        _nomeAluno.value = aluno.nome
    }

    fun updateFocoTreino(value: String) {
        _focoTreino.value = value
    }

    fun updateVigencia(value: String) {
        _vigencia.value = value
    }

    fun addExercicio(exercicioId: Long) {
        if (_selectedExercicios.value.any { it.exercicioId == exercicioId }) return

        _selectedExercicios.value = _selectedExercicios.value + ExercicioFichaTreino(
            exercicioId = exercicioId,
            series = 0,
            repeticoes = 0,
            descansoSegundos = 0
        )
    }

    fun removeExercicio(exercicioId: Long) {
        _selectedExercicios.value = _selectedExercicios.value.filterNot { it.exercicioId == exercicioId }
    }

    fun updateSeries(exercicioId: Long, value: String) {
        updateExercicioCampo(exercicioId) { atual ->
            atual.copy(series = value.toIntOrNull() ?: 0)
        }
    }

    fun updateRepeticoes(exercicioId: Long, value: String) {
        updateExercicioCampo(exercicioId) { atual ->
            atual.copy(repeticoes = value.toIntOrNull() ?: 0)
        }
    }

    fun updateDescansoSegundos(exercicioId: Long, value: String) {
        updateExercicioCampo(exercicioId) { atual ->
            atual.copy(descansoSegundos = value.toIntOrNull() ?: 0)
        }
    }

    fun save() {
        viewModelScope.launch {
            if (!formularioValido()) {
                _snackbarEvent.emit("Preencha os campos")
                return@launch
            }

            _isLoading.value = true
            try {
                val ficha = FichaTreino(
                    id = _editingId.value,
                    alunoId = _selectedAlunoId.value ?: 0,
                    focoTreino = _focoTreino.value.trim(),
                    vigencia = _vigencia.value.trim(),
                    exercicios = _selectedExercicios.value
                )

                if (_editingId.value == null) {
                    fichaRepository.create(ficha)
                    _snackbarEvent.emit("Ficha criada")
                } else {
                    fichaRepository.update(_editingId.value!!, ficha)
                    _snackbarEvent.emit("Ficha atualizada")
                }

                clearForm()
                _navigationEvent.emit(Unit)
            } catch (_: Exception) {
                _snackbarEvent.emit("Erro ao salvar ficha")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearForm() {
        _editingId.value = null
        _selectedAlunoId.value = null
        _nomeAluno.value = ""
        _focoTreino.value = ""
        _vigencia.value = ""
        _selectedExercicios.value = emptyList()
    }

    private fun formularioValido(): Boolean {
        return (_selectedAlunoId.value ?: 0) > 0 &&
            _focoTreino.value.isNotBlank() &&
            _vigencia.value.isNotBlank() &&
            _selectedExercicios.value.isNotEmpty() &&
            _selectedExercicios.value.all {
                it.series > 0 && it.repeticoes > 0 && it.descansoSegundos > 0
            }
    }

    private fun updateExercicioCampo(
        exercicioId: Long,
        transform: (ExercicioFichaTreino) -> ExercicioFichaTreino
    ) {
        _selectedExercicios.value = _selectedExercicios.value.map { atual ->
            if (atual.exercicioId == exercicioId) transform(atual) else atual
        }
    }
}







