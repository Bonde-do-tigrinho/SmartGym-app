package org.smartgym.viewModel.Adm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.smartgym.model.Adm.Unidade
import kotlin.random.Random

class UnidadesViewModel : ViewModel() {

    // Variável de estado para guardar a lista
    private val _listaUnidades = MutableStateFlow<List<Unidade>>(emptyList())
    val listaUnidades: StateFlow<List<Unidade>> = _listaUnidades.asStateFlow()

    // Variáveis de estado para CADA campo do formulário
    val idAtual = MutableStateFlow("")
    val nomeAtual = MutableStateFlow("")
    val enderecoAtual = MutableStateFlow("")
    val cidadeAtual = MutableStateFlow("")

    // Variável para controlar se a tela está mostrando a Lista ou o Formulário
    val mostrandoFormulario = MutableStateFlow(false)

    init {
        carregar() // Simula o carregamento inicial da API
    }

    // Carregar: Simulação do READ
    fun carregar() {
        if (_listaUnidades.value.isEmpty()) {
            _listaUnidades.value = listOf(
                Unidade("1", "Unidade Centro", "Rua Principal, 123", "São Paulo - SP", 320, 12),
                Unidade("2", "Unidade Zona Sul", "Av. Paulista, 456", "São Paulo - SP", 189, 8)
            )
        }
    }

    // Funcionalidade: Limpar Campos
    fun limparCampos() {
        idAtual.value = ""
        nomeAtual.value = ""
        enderecoAtual.value = ""
        cidadeAtual.value = ""
    }

    // Funcionalidade: Gravar (CREATE / UPDATE)
    fun gravar() {
        val listaAtualizada = _listaUnidades.value.toMutableList()

        if (idAtual.value.isEmpty()) {
            // É um registro novo (POST)
            val novaUnidade = Unidade(
                id = "id_${Random.nextInt(1000, 99999)}", // Usa o gerador aleatório nativo do Kotlin
                nome = nomeAtual.value,
                endereco = enderecoAtual.value,
                cidade = cidadeAtual.value
            )
            listaAtualizada.add(novaUnidade)
        } else {
            // É uma edição (PUT)
            val index = listaAtualizada.indexOfFirst { it.id == idAtual.value }
            if (index != -1) {
                listaAtualizada[index] = listaAtualizada[index].copy(
                    nome = nomeAtual.value,
                    endereco = enderecoAtual.value,
                    cidade = cidadeAtual.value
                )
            }
        }

        _listaUnidades.value = listaAtualizada
        limparCampos()
        mostrandoFormulario.value = false
        carregar() // Simula trazer os dados novamente atualizando a lista
    }

    // Funcionalidade: Preparar Edição
    fun prepararEdicao(unidade: Unidade) {
        idAtual.value = unidade.id
        nomeAtual.value = unidade.nome
        enderecoAtual.value = unidade.endereco
        cidadeAtual.value = unidade.cidade
        mostrandoFormulario.value = true // Abre o formulário
    }

    // Funcionalidade: Apagar (DELETE)
    fun apagar(idParaApagar: String) {
        val listaAtualizada = _listaUnidades.value.filter { it.id != idParaApagar }
        _listaUnidades.value = listaAtualizada
        carregar() // Simula atualizar os elementos
    }
}