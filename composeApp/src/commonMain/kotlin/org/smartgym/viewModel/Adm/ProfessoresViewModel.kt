package org.smartgym.viewModel.Adm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
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

class ProfessoresViewModel : ViewModel() {

    private val client = ApiClient.client

    private val _professores = MutableStateFlow<List<Usuario>>(emptyList())
    val professores: StateFlow<List<Usuario>> = _professores.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val professoresFiltrados: StateFlow<List<Usuario>> = combine(_professores, _searchQuery) { lista, query ->
        if (query.isBlank()) lista
        else lista.filter { it.nome.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _professores.value)

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent: SharedFlow<Unit> = _navigationEvent.asSharedFlow()

    init {
        carregarProfessores()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun carregarProfessores() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result: List<Usuario> = client
                    .get(ApiClient.getUrl("/api/professores"))
                    .body()
                _professores.value = result
            } catch (e: Exception) {
                println("ERRO AO CARREGAR PROFESSORES: ${e.message}")
                _errorMessage.value = "Erro ao carregar professores: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun adicionarProfessor(
        nome: String,
        email: String,
        telefone: String,
        cpf: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val novoProfessor = Usuario(
                    id = null,
                    nome = nome,
                    email = email,
                    telefone = telefone,
                    cpf = cpf,
                    status = true,
                    role = "PROFESSOR",
                    plano = null,
                    treinoAtual = null,
                    focoTreino = null,
                    planoVencimento = null,
                    planoValor = null
                )

                val url = ApiClient.getUrl("/api/professores")
                println("ENVIANDO PARA: $url")
                println("DADOS: $novoProfessor")

                val response = client.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(novoProfessor)
                }

                println("STATUS RESPONSE: ${response.status}")

                carregarProfessores()
                _snackbarEvent.emit("Professor cadastrado com sucesso!")
                _navigationEvent.emit(Unit)

            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Erro ao adicionar professor: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletarProfessor(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                client.delete(ApiClient.getUrl("/api/professores/$id"))
                carregarProfessores()
                _snackbarEvent.emit("Professor deletado com sucesso!")
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao deletar professor: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun editarProfessor(usuario: Usuario) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                client.put(ApiClient.getUrl("/api/professores/${usuario.id}")) {
                    contentType(ContentType.Application.Json)
                    setBody(usuario)
                }

                carregarProfessores()
                _snackbarEvent.emit("Professor atualizado com sucesso!")
                _navigationEvent.emit(Unit)

            } catch (e: Exception) {
                _errorMessage.value = "Erro ao editar professor: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}