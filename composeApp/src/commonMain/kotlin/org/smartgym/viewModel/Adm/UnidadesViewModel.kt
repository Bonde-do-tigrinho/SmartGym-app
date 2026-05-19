package org.smartgym.viewModel.Adm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.smartgym.model.Adm.Unidade
import org.smartgym.repository.ApiUnidadeRepository

class UnidadesViewModel : ViewModel() {

    private val repository = ApiUnidadeRepository()

    private val _listaUnidades = MutableStateFlow<List<Unidade>>(emptyList())
    val listaUnidades: StateFlow<List<Unidade>> = _listaUnidades.asStateFlow()

    val idAtual = MutableStateFlow<Int?>(null) // Mudamos para Int?
    val nomeAtual = MutableStateFlow("")
    val enderecoAtual = MutableStateFlow("")
    val cidadeAtual = MutableStateFlow("")

    val mostrandoFormulario = MutableStateFlow(false)

    init {
        carregar()
    }

    // Busca da API real
    fun carregar() {
        viewModelScope.launch {
            try {
                _listaUnidades.value = repository.buscarTodas()
            } catch (e: Exception) {
                println("Erro ao carregar unidades: ${e.message}")
            }
        }
    }

    fun limparCampos() {
        idAtual.value = null
        nomeAtual.value = ""
        enderecoAtual.value = ""
        cidadeAtual.value = ""
    }

    // Salva na API real
    fun gravar() {
        viewModelScope.launch {
            try {
                val unidade = Unidade(
                    id = idAtual.value,
                    nome = nomeAtual.value,
                    endereco = enderecoAtual.value,
                    cidade = cidadeAtual.value
                )
                repository.salvar(unidade)
                limparCampos()
                mostrandoFormulario.value = false
                carregar() // Atualiza a tela puxando do banco novamente
            } catch (e: Exception) {
                println("Erro ao gravar unidade: ${e.message}")
            }
        }
    }

    fun prepararEdicao(unidade: Unidade) {
        idAtual.value = unidade.id
        nomeAtual.value = unidade.nome
        enderecoAtual.value = unidade.endereco
        cidadeAtual.value = unidade.cidade
        mostrandoFormulario.value = true
    }

    // Apaga na API real
    fun apagar(idParaApagar: Int) {
        viewModelScope.launch {
            try {
                repository.apagar(idParaApagar)
                carregar() // Atualiza a tela
            } catch (e: Exception) {
                println("Erro ao apagar unidade: ${e.message}")
            }
        }
    }
}