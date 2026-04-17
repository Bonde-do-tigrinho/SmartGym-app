import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.smartgym.model.Adm.Maquina
import org.smartgym.repository.ApiMaquinasRepository

class MaquinaViewModel : ViewModel() {

    // Instancia o repositório que você criou
    private val repository = ApiMaquinasRepository()

    // Estado: Lista de máquinas (para a tela de listagem)
    private val _maquinas = MutableStateFlow<List<Maquina>>(emptyList())
    val maquinas = _maquinas.asStateFlow()

    // Estado: Única máquina (para tela de detalhes ou formulário de edição)
    private val _maquinaSelecionada = MutableStateFlow<Maquina?>(null)
    val maquinaSelecionada = _maquinaSelecionada.asStateFlow()

    // Estado: Controle de carregamento (Loading spinner na UI)
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Eventos disparados para a UI (Toast, Snackbar, navegação)
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    init {
        // Opcional: Já busca as máquinas assim que a ViewModel é criada
        carregarMaquinas()
    }

    // --- MÉTODOS DE LEITURA (GET) ---

    fun carregarMaquinas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _maquinas.value = repository.getAll()
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao carregar máquinas: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun buscarPorNome(nome: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Se o texto for vazio, carrega todas. Se não, busca na API
                if (nome.isBlank()) {
                    _maquinas.value = repository.getAll()
                } else {
                    _maquinas.value = repository.getByNome(nome)
                }
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao buscar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun carregarPorId(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _maquinaSelecionada.value = repository.getById(id)
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao carregar detalhes: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- MÉTODOS DE ESCRITA (POST, PUT, DELETE) ---

    fun criarMaquina(maquina: Maquina) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.create(maquina)
                _snackbarEvent.emit("Máquina criada com sucesso!")
                carregarMaquinas() // Atualiza a lista na tela após criar
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao criar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun atualizarMaquina(id: Long, maquina: Maquina) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.update(id, maquina)
                _snackbarEvent.emit("Máquina atualizada com sucesso!")
                carregarMaquinas() // Atualiza a lista na tela após editar
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao atualizar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletarMaquina(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.delete(id)
                _snackbarEvent.emit("Máquina excluída com sucesso!")
                carregarMaquinas() // Atualiza a lista removendo o item deletado
            } catch (e: Exception) {
                _snackbarEvent.emit("Erro ao excluir: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- UTILITÁRIOS ---

    // Útil para limpar os dados quando o usuário fechar a tela de detalhes/edição
    fun limparSelecao() {
        _maquinaSelecionada.value = null
    }
}