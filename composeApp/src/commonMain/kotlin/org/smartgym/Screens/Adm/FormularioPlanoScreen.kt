package org.smartgym.Screens.Adm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.smartgym.model.Adm.Plano
import org.smartgym.viewModel.Adm.PlanoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioPlanoScreen(
    viewModel: PlanoViewModel,
    planoInicial: Plano?,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onVoltar: () -> Unit
) {
    val isEditando = planoInicial != null
    val scope = rememberCoroutineScope()

    var nome by remember { mutableStateOf(planoInicial?.nome ?: "") }
    var descricao by remember { mutableStateOf(planoInicial?.descricao ?: "") }
    var valorInput by remember { mutableStateOf(planoInicial?.valor?.toString() ?: "") }
    var duracaoMeses by remember { mutableStateOf(planoInicial?.duracaoMeses ?: 1) }
    var ativo by remember { mutableStateOf(planoInicial?.ativo ?: true) }

    var expandedDropdown by remember { mutableStateOf(false) }
    val opcoesDuracao = listOf(
        1 to "Mensal (1 Mês)",
        3 to "Trimestral (3 Meses)",
        6 to "Semestral (6 Meses)",
        12 to "Anual (12 Meses)"
    )

    fun validarCampos(): Boolean {
        return nome.isNotBlank() && descricao.isNotBlank() && valorInput.toDoubleOrNull() != null
    }

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp)
    ) {
        // Cabeçalho
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            IconButton(
                onClick = onVoltar,
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = if (isEditando) "Editar Plano" else "Novo Plano",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                // NOME
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do Plano (Ex: Plano Black)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                // DESCRIÇÃO
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Benefícios e Descrição") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )

                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // VALOR
                    OutlinedTextField(
                        value = valorInput,
                        onValueChange = {
                            // Permite apenas números e um ponto para decimais
                            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                                valorInput = it
                            }
                        },
                        label = { Text("Valor Mensal (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // DURAÇÃO (Dropdown)
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = opcoesDuracao.find { it.first == duracaoMeses }?.second ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Duração") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Selecionar") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { expandedDropdown = true }
                        )
                        DropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            opcoesDuracao.forEach { (meses, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        duracaoMeses = meses
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // STATUS
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Status", fontWeight = FontWeight.Bold)
                            Text(
                                text = if (ativo) "Ativo para vendas" else "Pausado",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(checked = ativo, onCheckedChange = { ativo = it })
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                if (validarCampos()) {
                    val plano = Plano(
                        id = planoInicial?.id,
                        nome = nome,
                        descricao = descricao,
                        valor = valorInput.toDoubleOrNull() ?: 0.0,
                        duracaoMeses = duracaoMeses,
                        ativo = ativo
                    )

                    if (isEditando) {
                        plano.id?.let { viewModel.atualizarPlano(it, plano) }
                    } else {
                        viewModel.criarPlano(plano)
                    }
                    onVoltar()
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Preencha todos os campos corretamente.")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(if (isEditando) "Atualizar Plano" else "Salvar Novo Plano", fontWeight = FontWeight.Bold)
        }
    }
}