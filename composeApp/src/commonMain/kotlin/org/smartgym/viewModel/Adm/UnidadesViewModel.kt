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

    val idAtual = MutableStateFlow<Int?>(null)
    val nomeAtual = MutableStateFlow("")
    val enderecoAtual = MutableStateFlow("")
    val cidadeAtual = MutableStateFlow("")

    val mostrandoFormulario = MutableStateFlow(false)

    // Contagens visuais simuladas por nome de unidade
    private val contagensSimuladas = mapOf(
        "Unidade Centro"     to Pair(142, 8),
        "Unidade Zona Sul"   to Pair(98,  5),
        "Unidade Zona Oeste" to Pair(80, 6)
    )

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            try {
                val unidades = repository.buscarTodas()
                // Injeta contagens visuais com base no nome;
                // se não encontrar no mapa, usa valores padrão
                _listaUnidades.value = unidades.mapIndexed { index, unidade ->
                    val (alunos, instrutores) = contagensSimuladas[unidade.nome]
                        ?: Pair(80 + index * 15, 4 + index)
                    unidade.copy(alunos = alunos, instrutores = instrutores)
                }
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
                carregar()
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

    fun apagar(idParaApagar: Int) {
        viewModelScope.launch {
            try {
                repository.apagar(idParaApagar)
                carregar()
            } catch (e: Exception) {
                println("Erro ao apagar unidade: ${e.message}")
            }
        }
    }
}