package org.smartgym.viewModel.aluno

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.smartgym.network.ApiClient

@kotlinx.serialization.Serializable
data class UsuarioPerfilDto(
    val id: Int = 0,
    val nome: String = "",
    val email: String = "",
    val role: String = "",
    val cpf: String? = null,
    val telefone: String? = null,
    val plano: PlanoDto? = null,
    val planoVencimento: String? = null,
    val professorId: Int? = null,
    val professorNome: String? = null,
    val dataNascimento: String? = null,
    val altura: Double? = null,
    val peso: Double? = null,
    val dataCadastro: String? = null,
    val status: Boolean = true,
    val emailVerificado: Boolean = false
)

@Serializable
data class PlanoDto(
    val id: Int,
    val nome: String,
    val descricao: String? = null,
    val valor: Double,
    val duracaoMeses: Int,
    val ativo: Boolean
)

class AlunoPerfilViewModel : ViewModel() {

    private val _perfil = MutableStateFlow<UsuarioPerfilDto?>(null)
    val perfil: StateFlow<UsuarioPerfilDto?> = _perfil

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro

    init {
        carregarPerfil()
    }

    fun carregarPerfil() {
        viewModelScope.launch {
            _isLoading.value = true
            _erro.value = null
            try {
                val response = ApiClient.client.get(ApiClient.getUrl("/api/usuarios/me"))
                _perfil.value = response.body<UsuarioPerfilDto>()
            } catch (e: Exception) {
                _erro.value = "Erro ao carregar perfil: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}