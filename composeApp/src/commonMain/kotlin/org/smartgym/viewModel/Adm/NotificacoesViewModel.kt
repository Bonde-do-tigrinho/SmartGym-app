package org.smartgym.viewModel.Adm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.smartgym.model.Adm.Notificacao
import org.smartgym.repository.ApiNotificacaoRepository

class NotificacoesViewModel : ViewModel() {
    private val repository = ApiNotificacaoRepository()

    private val _notificacoes = MutableStateFlow<List<Notificacao>>(emptyList())
    val notificacoes: StateFlow<List<Notificacao>> = _notificacoes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun carregarNotificacoes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val lista = repository.buscarTodas().sortedByDescending { it.id ?: 0 }
                _notificacoes.value = lista
            } catch (e: Exception) {
                println("Erro ao carregar notificacoes: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun apagarNotificacao(id: Int) {
        viewModelScope.launch {
            try {
                repository.apagar(id)
                carregarNotificacoes()
            } catch (e: Exception) {
                println("Erro ao apagar: ${e.message}")
            }
        }
    }

    // Apenas formata a data para dd/mm/yyyy de forma infalível
    fun formatarData(dataString: String?): String {
        if (dataString.isNullOrEmpty()) return ""
        val dataPura = dataString.split("T").firstOrNull() ?: return dataString
        val partes = dataPura.split("-")
        if (partes.size < 3) return dataPura

        val ano = partes[0]
        val mes = partes[1]
        val dia = partes[2]

        return "$dia/$mes/$ano"
    }

    fun ehNotificacaoNova(notificacao: Notificacao): Boolean {
        val avisoMaisRecente = notificacoes.value.firstOrNull()
        return avisoMaisRecente != null && notificacao.id == avisoMaisRecente.id
    }
}