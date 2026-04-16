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
import org.smartgym.model.Adm.Aluno
import org.smartgym.network.ApiClient

class AlunosViewModel : ViewModel() {

    private val client = ApiClient.client
    private val _alunos = MutableStateFlow<List<Aluno>>(emptyList())
    val alunos: StateFlow<List<Aluno>> = _alunos.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val alunosFiltrados: StateFlow<List<Aluno>> = combine(_alunos, _searchQuery) { lista, query ->
        if (query.isBlank()) lista
        else lista.filter { it.nome.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), _alunos.value)

    init{
        carregarAlunos()
    }
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun carregarAlunos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val url = ApiClient.getUrl("/alunos")
                println("CHAMANDO API: $url")

                val result: List<Aluno> = client
                    .get(url)
                    .body()

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
        plano: String,
        status: Boolean
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val novoAluno = Aluno(
                    id = 0,
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
                client.post(ApiClient.getUrl("/alunos")) {
                    contentType(ContentType.Application.Json)
                    setBody(novoAluno)
                }
                carregarAlunos() // Atualiza a lista após salvar
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
                client.delete(ApiClient.getUrl("/alunos/$id"))
                carregarAlunos() // Atualiza a lista após deletar
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao deletar aluno: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun editarAluno(aluno: Aluno) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                client.put(ApiClient.getUrl("/alunos/${aluno.id}")) {
                    contentType(ContentType.Application.Json)
                    setBody(aluno)
                }
                carregarAlunos() // Atualiza a lista após editar
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao editar aluno: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}