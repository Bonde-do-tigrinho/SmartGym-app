package org.smartgym.viewModel.aluno

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.smartgym.network.ApiClient


@Serializable
data class ProfessorDto(
    val id: Int,
    val nome: String,
    val email: String
)

@Serializable
data class CompletarPerfilRequest(
    val planoId: Int,
    val professorId: Int
)

sealed class CompletarPerfilState {
    object Idle : CompletarPerfilState()
    object Loading : CompletarPerfilState()
    object Success : CompletarPerfilState()
    data class Error(val message: String) : CompletarPerfilState()
}

class CompletarPerfilViewModel : ViewModel() {

    private val _planos = MutableStateFlow<List<PlanoDto>>(emptyList())
    val planos: StateFlow<List<PlanoDto>> = _planos

    private val _professores = MutableStateFlow<List<ProfessorDto>>(emptyList())
    val professores: StateFlow<List<ProfessorDto>> = _professores

    private val _state = MutableStateFlow<CompletarPerfilState>(CompletarPerfilState.Idle)
    val state: StateFlow<CompletarPerfilState> = _state

    var planoSelecionado = MutableStateFlow<PlanoDto?>(null)
    var professorSelecionado = MutableStateFlow<ProfessorDto?>(null)

    init {
        carregarDados()
    }

    private fun carregarDados() {
        viewModelScope.launch {
            _state.value = CompletarPerfilState.Loading
            try {
                _planos.value = ApiClient.client
                    .get(ApiClient.getUrl("/api/planos"))
                    .body()

                _professores.value = ApiClient.client
                    .get(ApiClient.getUrl("/api/professores"))
                    .body()

                _state.value = CompletarPerfilState.Idle
            } catch (e: Exception) {
                _state.value = CompletarPerfilState.Error("Erro ao carregar dados: ${e.message}")
            }
        }
    }

    fun completarPerfil(onSuccess: () -> Unit) {
        val plano = planoSelecionado.value
        val professor = professorSelecionado.value

        if (plano == null) { _state.value = CompletarPerfilState.Error("Por favor, selecione um plano."); return }
        if (professor == null) { _state.value = CompletarPerfilState.Error("Por favor, selecione um professor."); return }

        _state.value = CompletarPerfilState.Loading

        viewModelScope.launch {
            try {
                ApiClient.client.put(ApiClient.getUrl("/api/usuarios/completar-perfil")) {
                    contentType(ContentType.Application.Json)
                    setBody(CompletarPerfilRequest(planoId = plano.id, professorId = professor.id))
                }

                _state.value = CompletarPerfilState.Success
                onSuccess()
            } catch (e: Exception) {
                _state.value = CompletarPerfilState.Error("Erro ao salvar perfil: ${e.message}")
            }
        }
    }
}