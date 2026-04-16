package org.smartgym.viewModel.Professor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.smartgym.model.professor.Exercicio
import org.smartgym.model.professor.TipoExercicio
import org.smartgym.repository.ExercicioRepository

class ExerciciosViewModel(private val repository: ExercicioRepository) : ViewModel() {

    private val _exercicios = MutableStateFlow<List<Exercicio>>(emptyList())
    val exercicios: StateFlow<List<Exercicio>> = _exercicios.asStateFlow()

    private val _nome = MutableStateFlow("")
    val nome: StateFlow<String> = _nome.asStateFlow()

    private val _descricao = MutableStateFlow("")
    val descricao: StateFlow<String> = _descricao.asStateFlow()

    private val _tipo = MutableStateFlow(TipoExercicio.LIVRE)
    val tipo: StateFlow<TipoExercicio> = _tipo.asStateFlow()

    private val _maquinaId = MutableStateFlow<Long?>(null)
    val maquinaId: StateFlow<Long?> = _maquinaId.asStateFlow()

    private val _editingId = MutableStateFlow<Long?>(null)
    val editingId: StateFlow<Long?> = _editingId.asStateFlow()

    fun updateNome(value: String) { _nome.value = value }
    fun updateDescricao(value: String) { _descricao.value = value }
    fun updateTipo(value: TipoExercicio) { _tipo.value = value }
    fun updateMaquinaId(value: Long?) { _maquinaId.value = value }

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
            _exercicios.value = repository.getByNome(nome)
        }
    }

    fun loadById(id: Long) {
        viewModelScope.launch {
            repository.getById(id)?.let { exercicio ->
                _editingId.value = id
                _nome.value = exercicio.nome
                _descricao.value = exercicio.descricao
                _tipo.value = exercicio.tipo
                _maquinaId.value = exercicio.maquinaId
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

    fun create() {
        viewModelScope.launch {
            val exercicio = Exercicio(
                nome = _nome.value,
                descricao = _descricao.value,
                tipo = _tipo.value,
                maquinaId = _maquinaId.value
            )
            repository.create(exercicio)
            loadAll()
            clearForm()
        }
    }

    fun update(id: Long) {
        viewModelScope.launch {
            val exercicio = Exercicio(
                id = id,
                nome = _nome.value,
                descricao = _descricao.value,
                tipo = _tipo.value,
                maquinaId = _maquinaId.value
            )
            repository.update(id, exercicio)
            loadAll()
            clearForm()
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
            loadAll()
        }
    }

    fun loadByMaquina(maquinaId: Long) {
        viewModelScope.launch {
            _exercicios.value = repository.getByMaquina(maquinaId)
        }
    }

     fun clearForm() {
        _editingId.value = null
        _nome.value = ""
        _descricao.value = ""
        _tipo.value = TipoExercicio.LIVRE
        _maquinaId.value = null
    }
}
