package org.smartgym.viewModel.Adm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.smartgym.model.Adm.Usuario
import org.smartgym.network.ApiClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.smartgym.model.Adm.Plano

class AlunosViewModel : ViewModel() {

    private val client = ApiClient.client

    private val basePaths = listOf("/api/alunos")

    private val _alunos = MutableStateFlow<List<Usuario>>(emptyList())
    val alunos: StateFlow<List<Usuario>> = _alunos.asStateFlow()

    private val _planosDisponiveis = MutableStateFlow<List<Plano>>(emptyList())
    val planosDisponiveis: StateFlow<List<Plano>> = _planosDisponiveis.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val alunosFiltrados: StateFlow<List<Usuario>> = combine(_alunos, _searchQuery) { lista, query ->
        if (query.isBlank()) lista
        else lista.filter { it.nome.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), _alunos.value)

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent: SharedFlow<Unit> = _navigationEvent.asSharedFlow()

    init {
        carregarAlunos()
    }

    private fun url(basePath: String, path: String = "") = ApiClient.getUrl("$basePath$path")

    private suspend fun <T> executeAlunoRequest(request: suspend (String) -> T): T {
        var lastError: Exception? = null

        for (basePath in basePaths) {
            try {
                return request(basePath)
            } catch (e: ResponseException) {
                if (e.response.status.value != 404 || basePath == basePaths.last()) {
                    val body = runCatching { e.response.bodyAsText() }.getOrNull().orEmpty()
                    val detalheBody = if (body.isNotBlank()) " | body=$body" else ""
                    throw IllegalStateException(
                        "Erro HTTP ${e.response.status.value} ao acessar ${url(basePath)}$detalheBody",
                        e
                    )
                }
                lastError = e
            } catch (e: Exception) {
                lastError = e
                if (basePath == basePaths.last()) {
                    throw IllegalStateException(
                        "Nao foi possivel concluir a operacao de alunos em ${url(basePath)}: ${e.message}",
                        e
                    )
                }
            }
        }

        throw IllegalStateException("Nao foi possivel concluir a operacao de alunos.", lastError)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun carregarAlunos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result: List<Usuario> = executeAlunoRequest { basePath ->
                    val resolvedUrl = url(basePath)
                    println("CHAMANDO API: $resolvedUrl")
                    client.get(resolvedUrl).body()
                }

                println("ALUNOS RECEBIDOS: $result")
                _alunos.value = result

            } catch (e: Exception) {
                println("ERRO AO CARREGAR ALUNOS: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Erro ao carregar alunos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun adicionarAluno(
        nome: String,
        email: String,
        telefone: String,
        cpf: String,
        plano: Plano?,
        status: Boolean
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val novoUsuario = Usuario(
                    id = null,
                    nome = nome,
                    email = email,
                    telefone = telefone,
                    cpf = cpf,
                    plano = plano,
                    status = status,
                    treinoAtual = null,
                    focoTreino = null,
                    planoVencimento = null,
                    planoValor = null
                )

                executeAlunoRequest { basePath ->
                    client.post(url(basePath)) {
                        contentType(ContentType.Application.Json)
                        setBody(novoUsuario)
                    }
                }

                carregarAlunos()
                _snackbarEvent.emit("Usuario cadastrado com sucesso!")
                _navigationEvent.emit(Unit)

            } catch (e: Exception) {
                _errorMessage.value = "Erro ao adicionar aluno: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletarAluno(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                executeAlunoRequest { basePath ->
                    client.delete(url(basePath, "/$id"))
                }
                carregarAlunos()
                _snackbarEvent.emit("Usuario deletado com sucesso!")

            } catch (e: Exception) {
                _errorMessage.value = "Erro ao deletar aluno: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun editarAluno(usuario: Usuario) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                executeAlunoRequest { basePath ->
                    client.put(url(basePath, "/${usuario.id}")) {
                        contentType(ContentType.Application.Json)
                        setBody(usuario)
                    }
                }

                carregarAlunos()
                _snackbarEvent.emit("Usuario atualizado com sucesso!")
                _navigationEvent.emit(Unit)

            } catch (e: Exception) {
                _errorMessage.value = "Erro ao editar usuario: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun carregarPlanosDisponiveis() {
        viewModelScope.launch {
            try {
                val result: List<Plano> = executeAlunoRequest { basePath ->
                    // Ajuste o endpoint caso no Spring seja diferente (ex: "/api/planos")
                    val urlPlanos = ApiClient.getUrl("/api/planos")
                    println("📡 [KTOR GET] Buscando planos em: $urlPlanos")
                    client.get(urlPlanos).body()
                }
                println("✅ PLANOS CARREGADOS DO BANCO: $result")
                _planosDisponiveis.value = result
            } catch (e: Exception) {
                println("🚨 ERRO AO CARREGAR PLANOS: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    fun clearSuccess() {
        _successMessage.value = null
    }
}