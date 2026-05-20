import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                _maquinasIot.value = repository.getAll()
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
                repository.create(maquinaIot)
                _snackbarEvent.emit("Máquina IOT criada com sucesso!")
                carregarMaquinasIot()
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao criar: ${e.message}")
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
                _snackbarEvent.emit("Máquina IOT atualizada com sucesso!")
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