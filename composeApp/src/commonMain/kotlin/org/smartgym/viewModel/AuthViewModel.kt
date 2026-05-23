package org.smartgym.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.smartgym.UserRole
import org.smartgym.repository.ApiAuthRepository

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val repository = ApiAuthRepository()

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun login(email: String, senha: String, onSuccess: (UserRole) -> Unit) {
        if (email.isBlank()) { _state.value = AuthState.Error("Informe seu email"); return }
        if (!email.contains("@")) { _state.value = AuthState.Error("Email inválido"); return }
        if (senha.isBlank()) { _state.value = AuthState.Error("Informe sua senha"); return }

        _state.value = AuthState.Loading
        viewModelScope.launch {
            val resultado = repository.login(email.trim(), senha)
            if (resultado.sucesso && resultado.papel != null) {
                val role = when (resultado.papel.lowercase()) {
                    "aluno"     -> UserRole.ALUNO
                    "professor" -> UserRole.PROFESSOR
                    "admin"     -> UserRole.ADMIN
                    else        -> null
                }
                if (role != null) {
                    _state.value = AuthState.Success("Bem-vindo!")
                    onSuccess(role)
                } else {
                    _state.value = AuthState.Error(" Perfil desconhecido: ${resultado.papel}")
                }
            } else {
                _state.value = AuthState.Error(" ${resultado.mensagem}")
            }
        }
    }

    fun registrar(
        nome: String,
        email: String,
        telefone: String,
        senha: String,
        confirmarSenha: String,
        onSuccess: () -> Unit
    ) {
        if (nome.isBlank()) { _state.value = AuthState.Error("Informe seu nome"); return }
        if (email.isBlank() || !email.contains("@")) { _state.value = AuthState.Error("Email inválido"); return }
        if (telefone.length != 11) { _state.value = AuthState.Error("Telefone inválido"); return }
        if (senha.length < 6) { _state.value = AuthState.Error("Senha deve ter no mínimo 6 caracteres"); return }
        if (senha != confirmarSenha) { _state.value = AuthState.Error("As senhas não coincidem"); return }

        _state.value = AuthState.Loading
        viewModelScope.launch {
            val resultado = repository.registrar(nome, email, telefone, senha)
            if (resultado.sucesso) {
                _state.value = AuthState.Success(resultado.mensagem)
                onSuccess()
            } else {
                _state.value = AuthState.Error("${resultado.mensagem}")
            }
        }
    }

    fun recuperarSenha(email: String, onSuccess: () -> Unit) {
        if (email.isBlank() || !email.contains("@")) {
            _state.value = AuthState.Error("Email inválido")
            return
        }
        _state.value = AuthState.Loading
        viewModelScope.launch {
            val resultado = repository.recuperarSenha(email.trim())
            _state.value = AuthState.Success("Se este email estiver cadastrado, você receberá as instruções.")
            onSuccess()
        }
    }

    fun resetarSenha(token: String, novaSenha: String, confirmarSenha: String, onSuccess: () -> Unit) {
        if (novaSenha.length < 6) { _state.value = AuthState.Error("⚠️ Senha deve ter no mínimo 6 caracteres"); return }
        if (novaSenha != confirmarSenha) { _state.value = AuthState.Error("⚠️ As senhas não coincidem"); return }

        _state.value = AuthState.Loading
        viewModelScope.launch {
            val resultado = repository.resetarSenha(token, novaSenha)
            if (resultado.sucesso) {
                _state.value = AuthState.Success("Senha redefinida com sucesso!")
                onSuccess()
            } else {
                _state.value = AuthState.Error("${resultado.mensagem}")
            }
        }
    }

    fun resetState() {
        _state.value = AuthState.Idle
    }
}