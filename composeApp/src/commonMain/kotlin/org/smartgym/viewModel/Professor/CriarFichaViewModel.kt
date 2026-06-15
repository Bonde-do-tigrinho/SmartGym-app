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
import org.smartgym.model.professor.TreinoDia
import org.smartgym.repository.AlunoRepository
import org.smartgym.repository.ApiExercicioRepository
import org.smartgym.repository.FichaTreinoRepository
import org.smartgym.util.formatDateToUi

class CriarFichaViewModel(
    private val alunoRepository: AlunoRepository,
    private val exercicioRepository: ApiExercicioRepository,
    private val fichaRepository: FichaTreinoRepository
) : ViewModel() {

    private val _abaSelecionada = MutableStateFlow(0)
    val abaSelecionada = _abaSelecionada.asStateFlow()

    private val _mapaDias = MutableStateFlow(
        mapOf(
            0 to TreinoDia(letra = "A", focoTreino = "", exercicios = emptyList()),
            1 to TreinoDia(letra = "B", focoTreino = "", exercicios = emptyList()),
            2 to TreinoDia(letra = "C", focoTreino = "", exercicios = emptyList())
        )
    )
    val mapaDias = _mapaDias.asStateFlow()

    private val _alunos = MutableStateFlow<List<AlunoResumido>>(emptyList())
    val alunos: StateFlow<List<AlunoResumido>> = _alunos.asStateFlow()

    private val _exercicios = MutableStateFlow<List<Exercicio>>(emptyList())
    val exercicios: StateFlow<List<Exercicio>> = _exercicios.asStateFlow()

    private val _selectedAlunoId = MutableStateFlow<Int?>(null)
    val selectedAlunoId: StateFlow<Int?> = _selectedAlunoId.asStateFlow()

    private val _nomeAluno = MutableStateFlow("")
    val nomeAluno: StateFlow<String> = _nomeAluno.asStateFlow()

    private val _vigencia = MutableStateFlow("")
    val vigencia: StateFlow<String> = _vigencia.asStateFlow()

    private val _editingId = MutableStateFlow<Int?>(null)
    val editingId: StateFlow<Int?> = _editingId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent: SharedFlow<Unit> = _navigationEvent.asSharedFlow()

    fun selecionarAba(index: Int) {
        _abaSelecionada.value = index
    }


    fun updateFocoDoDia(foco: String) {
        val aba = _abaSelecionada.value
        val diaAtual = _mapaDias.value[aba] ?: return
        _mapaDias.value = _mapaDias.value + (aba to diaAtual.copy(focoTreino = foco))
    }

    fun addExercicio(exercicioId: Int) {
        val aba = _abaSelecionada.value
        val diaAtual = _mapaDias.value[aba] ?: return

        if (diaAtual.exercicios.any { it.exercicioId == exercicioId }) return

        val novosExercicios = diaAtual.exercicios + ExercicioFichaTreino(
            exercicioId = exercicioId,
            series = 0,
            repeticoes = 0,
            descansoSegundos = 0
        )
        _mapaDias.value = _mapaDias.value + (aba to diaAtual.copy(exercicios = novosExercicios))
    }

    fun removeExercicio(exercicioId: Int) {
        val aba = _abaSelecionada.value
        val diaAtual = _mapaDias.value[aba] ?: return

        val filtrados = diaAtual.exercicios.filterNot { it.exercicioId == exercicioId }
        _mapaDias.value = _mapaDias.value + (aba to diaAtual.copy(exercicios = filtrados))
    }

    fun updateSeries(exercicioId: Int, value: String) {
        updateExercicioCampo(exercicioId) { it.copy(series = value.toIntOrNull() ?: 0) }
    }

    fun updateRepeticoes(exercicioId: Int, value: String) {
        updateExercicioCampo(exercicioId) { it.copy(repeticoes = value.toIntOrNull() ?: 0) }
    }

    fun updateDescansoSegundos(exercicioId: Int, value: String) {
        updateExercicioCampo(exercicioId) { it.copy(descansoSegundos = value.toIntOrNull() ?: 0) }
    }

    private fun updateExercicioCampo(
        exercicioId: Int,
        transform: (ExercicioFichaTreino) -> ExercicioFichaTreino
    ) {
        val aba = _abaSelecionada.value
        val diaAtual = _mapaDias.value[aba] ?: return

        val exerciciosAtualizados = diaAtual.exercicios.map { atual ->
            if (atual.exercicioId == exercicioId) transform(atual) else atual
        }
        _mapaDias.value = _mapaDias.value + (aba to diaAtual.copy(exercicios = exerciciosAtualizados))
    }


    fun loadInitialData(force: Boolean = false) {
        if (!force && _alunos.value.isNotEmpty() && _exercicios.value.isNotEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                _alunos.value = alunoRepository.getMeusAlunos().sortedBy { it.nome.lowercase() }
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

    suspend fun obterIdFichaDoAluno(alunoId: Int): Int? {

        return fichaRepository.getAll().find { it.alunoId == alunoId }?.id
    }

    fun obterGrupoMuscular(exercicioId: Int): String {
        val exercicioEncontrado = _exercicios.value.find { it.id == exercicioId }
        return exercicioEncontrado?.grupoMuscular ?: "Geral"
    }

    fun loadById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                fichaRepository.getById(id)?.let { ficha ->
                    _editingId.value = ficha.id
                    _selectedAlunoId.value = ficha.alunoId
                    _nomeAluno.value = _alunos.value.firstOrNull { it.id == ficha.alunoId }?.nome.orEmpty()
                    _vigencia.value = formatDateToUi(ficha.vigencia)

                    val novoMapa = mapOf(
                        0 to (ficha.rotinaDias.find { it.letra == "A" } ?: TreinoDia(letra = "A", focoTreino = "")),
                        1 to (ficha.rotinaDias.find { it.letra == "B" } ?: TreinoDia(letra = "B", focoTreino = "")),
                        2 to (ficha.rotinaDias.find { it.letra == "C" } ?: TreinoDia(letra = "C", focoTreino = ""))
                    )
                    _mapaDias.value = novoMapa
                }
            } catch (_: Exception) {
                _snackbarEvent.emit("Erro ao carregar ficha")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun prepareForEdit(id: Int) = loadById(id)

    fun updateSelectedAluno(aluno: AlunoResumido) {
        _selectedAlunoId.value = aluno.id
        _nomeAluno.value = aluno.nome
    }

    fun updateVigencia(value: String) {
        _vigencia.value = value
    }


    fun save() {
        viewModelScope.launch {
            if (!formularioValido()) {
                _snackbarEvent.emit("Preencha os campos e os exercícios corretamente")
                return@launch
            }

            _isLoading.value = true
            try {
                val partesData = _vigencia.value.trim().split("/")
                val vigenciaFormatadaISO = if (partesData.size == 3) {
                    "${partesData[2]}-${partesData[1]}-${partesData[0]}T00:00:00.000Z"
                } else {
                    _vigencia.value.trim()
                }

                val todosOsDiasDoMapa = _mapaDias.value.values.toList()
                println("=== DEBUG FRONTEND ===")
                todosOsDiasDoMapa.forEach { dia ->
                    println("Aba ${dia.letra} -> Foco: ${dia.focoTreino} | Qtd Exercícios: ${dia.exercicios.size}")
                }

                val diasParaEnviar = _mapaDias.value.values.filter { it.exercicios.isNotEmpty() }.toList()
                println("Dias reais que serão enviados para a API: ${diasParaEnviar.size}")

                val ficha = FichaTreino(
                    id = _editingId.value,
                    alunoId = _selectedAlunoId.value ?: 0,
                    vigencia = vigenciaFormatadaISO,
                    rotinaDias = diasParaEnviar
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
            } catch (e: Exception) {
                println("🚨 ERRO CRÍTICO NO KTOR FRONTEND: ${e.message}")
                e.printStackTrace()
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
        _vigencia.value = ""
        _abaSelecionada.value = 0
        _mapaDias.value = mapOf(
            0 to TreinoDia(letra = "A", focoTreino = "", exercicios = emptyList()),
            1 to TreinoDia(letra = "B", focoTreino = "", exercicios = emptyList()),
            2 to TreinoDia(letra = "C", focoTreino = "", exercicios = emptyList())
        )
    }

    private fun formularioValido(): Boolean {
        val alunoOk = (_selectedAlunoId.value ?: 0) > 0
        val vigenciaOk = _vigencia.value.isNotBlank()

        val todosDias = _mapaDias.value.values
        val temAoMenosUmTreino = todosDias.any { it.exercicios.isNotEmpty() && it.focoTreino.isNotBlank() }

        val todosExerciciosValidos = todosDias.flatMap { it.exercicios }.all {
            it.series > 0 && it.repeticoes > 0 && it.descansoSegundos > 0
        }

        return alunoOk && vigenciaOk && temAoMenosUmTreino && todosExerciciosValidos
    }
}