package org.smartgym.viewModel.Professor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.smartgym.model.professor.Exercicio
import org.smartgym.model.professor.TipoExercicio
import org.smartgym.repository.ApiExercicioRepository

class ExerciciosViewModel() : ViewModel() {

    private val repository = ApiExercicioRepository()
    private val _exercicios = MutableStateFlow<List<Exercicio>>(emptyList())
    val exercicios: StateFlow<List<Exercicio>> = _exercicios.asStateFlow()

    private val _nome = MutableStateFlow("")
    val nome: StateFlow<String> = _nome.asStateFlow()

    private val _descricao = MutableStateFlow("")
    val descricao: StateFlow<String> = _descricao.asStateFlow()

    private val _tipo = MutableStateFlow(TipoExercicio.LIVRE)
    val tipo: StateFlow<TipoExercicio> = _tipo.asStateFlow()

    // --- NOVO: Estado do Grupo Muscular ---
    private val _grupoMuscular = MutableStateFlow("")
    val grupoMuscular: StateFlow<String> = _grupoMuscular.asStateFlow()

    private val _maquinaId = MutableStateFlow<Long?>(null)
    val maquinaId: StateFlow<Long?> = _maquinaId.asStateFlow()

    private val _editingId = MutableStateFlow<Long?>(null)
    val editingId: StateFlow<Long?> = _editingId.asStateFlow()

    fun updateNome(value: String) { _nome.value = value }
    fun updateDescricao(value: String) { _descricao.value = value }
    fun updateTipo(value: TipoExercicio) { _tipo.value = value }
    fun updateMaquinaId(value: Long?) { _maquinaId.value = value }

    // --- NOVO: Função para atualizar o Grupo Muscular ---
    fun updateGrupoMuscular(value: String) { _grupoMuscular.value = value }

    fun loadAll() {
        viewModelScope.launch {
            try {
                _exercicios.value = repository.getAll()
            } catch (e: Exception) {
                println("Erro ao carregar exercícios: ${e.message}")
                _exercicios.value = emptyList() // Deixa a lista vazia
            }
        }
    }

    fun loadByNome(nome: String) {
        viewModelScope.launch {
            try {
                _exercicios.value = repository.getByNome(nome)
            } catch (e: Exception) {
                println("Erro ao buscar exercícios: ${e.message}")
            }
        }
    }

    fun loadById(id: Long) {
        viewModelScope.launch {
            try {
                repository.getById(id)?.let { exercicio ->
                    _editingId.value = id
                    _nome.value = exercicio.nome
                    _descricao.value = exercicio.descricao
                    _tipo.value = exercicio.tipo
                    _grupoMuscular.value = exercicio.grupoMuscular ?: "" // --- NOVO: Carrega o grupo muscular (se houver) ---
                    _maquinaId.value = exercicio.maquinaId
                }
            } catch (e: Exception) {
                println("Erro ao carregar exercício por ID: ${e.message}")
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

    private fun create() { // Transformei em private pois a UI só deve chamar o "save"
        viewModelScope.launch {
            try {
                val exercicio = Exercicio(
                    nome = _nome.value,
                    descricao = _descricao.value,
                    tipo = _tipo.value,
                    grupoMuscular = if (_grupoMuscular.value.isNotBlank()) _grupoMuscular.value else null, // --- NOVO: Salva o grupo muscular ---
                    maquinaId = _maquinaId.value
                )
                repository.create(exercicio)
                loadAll()
                clearForm()
            } catch (e: Exception) {
                println("Erro ao criar exercício: ${e.message}")
            }
        }
    }

    private fun update(id: Long) { // Transformei em private pois a UI só deve chamar o "save"
        viewModelScope.launch {
            try {
                val exercicio = Exercicio(
                    id = id,
                    nome = _nome.value,
                    descricao = _descricao.value,
                    tipo = _tipo.value,
                    grupoMuscular = if (_grupoMuscular.value.isNotBlank()) _grupoMuscular.value else null, // --- NOVO: Atualiza o grupo muscular ---
                    maquinaId = _maquinaId.value
                )
                repository.update(id, exercicio)
                loadAll()
                clearForm()
            } catch (e: Exception) {
                println("Erro ao atualizar exercício: ${e.message}")
            }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            try {
                repository.delete(id)
                loadAll()
            } catch (e: Exception) {
                println("Erro ao deletar exercício: ${e.message}")
            }
        }
    }

    fun loadByMaquina(maquinaId: Long) {
        viewModelScope.launch {
            try {
                _exercicios.value = repository.getByMaquina(maquinaId)
            } catch (e: Exception) {
                println("Erro ao carregar exercícios por máquina: ${e.message}")
            }
        }
    }

    fun clearForm() {
        _editingId.value = null
        _nome.value = ""
        _descricao.value = ""
        _tipo.value = TipoExercicio.LIVRE
        _grupoMuscular.value = "" // --- NOVO: Limpa o campo do Grupo Muscular ---
        _maquinaId.value = null
    }
}