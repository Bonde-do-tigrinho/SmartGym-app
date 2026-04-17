package org.smartgym.Screens.Adm

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.smartgym.model.Adm.Plano
import org.smartgym.viewModel.Adm.PlanoViewModel


class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.take(8)
        val formatted = buildString {
            digits.forEachIndexed { i, c ->
                append(c)
                if (i == 1 || i == 3) append("/")
            }
        }
        val offsetMap = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 3) return offset + 1
                if (offset <= 8) return offset + 2
                return 10
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                if (offset <= 10) return offset - 2
                return 8
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMap)
    }
}

class TimeVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.take(4)
        val formatted = buildString {
            digits.forEachIndexed { i, c ->
                append(c)
                if (i == 1) append(":")
            }
        }
        val offsetMap = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 4) return offset + 1
                return 5
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                return 4
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMap)
    }
}

@Composable
fun PlanosScreen(viewModel: PlanoViewModel) {
    val planos by viewModel.planos.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val colors = MaterialTheme.colorScheme

    var mostrandoFormulario by remember { mutableStateOf(false) }

    var idEditando by remember { mutableStateOf<Int?>(null) }
    var nome by remember { mutableStateOf("") }
    var ativo by remember { mutableStateOf(true) }
    var dataFimNumeros by remember { mutableStateOf("") } // Ex: 31122026
    var horarioNumeros by remember { mutableStateOf("") } // Ex: 2359

    LaunchedEffect(Unit) {
        viewModel.carregarPlanos()
        viewModel.snackbarEvent.collect { mensagem ->
            snackbarHostState.showSnackbar(mensagem)
        }
    }

    fun limparCampos() {
        idEditando = null
        nome = ""
        ativo = true
        dataFimNumeros = ""
        horarioNumeros = ""
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = colors.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {

            Crossfade(targetState = mostrandoFormulario) { telaFormulario ->
                if (telaFormulario) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(if (idEditando == null) "Cadastrar novo Plano" else "Editar Plano", color = colors.onBackground, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Nome do Plano", color = colors.onSurfaceVariant, fontSize = 12.sp)
                        OutlinedTextField(
                            value = nome,
                            onValueChange = { nome = it },
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colors.primary, unfocusedBorderColor = colors.surfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Data Fim", color = colors.onSurfaceVariant, fontSize = 12.sp)
                                OutlinedTextField(
                                    value = dataFimNumeros,
                                    onValueChange = { if (it.length <= 8) dataFimNumeros = it.filter { char -> char.isDigit() } },
                                    placeholder = { Text("DD/MM/AAAA", color = Color.Gray) },
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    visualTransformation = DateVisualTransformation(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colors.primary, unfocusedBorderColor = colors.surfaceVariant)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Horário Limite", color = colors.onSurfaceVariant, fontSize = 12.sp)
                                OutlinedTextField(
                                    value = horarioNumeros,
                                    onValueChange = { if (it.length <= 4) horarioNumeros = it.filter { char -> char.isDigit() } },
                                    placeholder = { Text("HH:MM", color = Color.Gray) },
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    visualTransformation = TimeVisualTransformation(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colors.primary, unfocusedBorderColor = colors.surfaceVariant)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = ativo,
                                onCheckedChange = { ativo = it },
                                colors = CheckboxDefaults.colors(checkedColor = colors.primary, checkmarkColor = Color.Black)
                            )
                            Text("Plano Ativo para Vendas", color = colors.onBackground)
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    val dataFormatada = SeNumerosCompletos(dataFimNumeros, 8) {
                                        "${it.substring(4, 8)}-${it.substring(2, 4)}-${it.substring(0, 2)}"
                                    }

                                    val horaFormatada = SeNumerosCompletos(horarioNumeros, 4) {
                                        "${it.substring(0, 2)}:${it.substring(2, 4)}:00"
                                    }

                                    val planoParaSalvar = Plano(idEditando, nome, ativo, dataFormatada, horaFormatada)
                                    viewModel.salvarPlano(planoParaSalvar)
                                    limparCampos()
                                    mostrandoFormulario = false
                                },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Color.Black)
                            ) {
                                Text("Salvar", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { limparCampos() },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onBackground)
                            ) {
                                Text("Limpar")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { mostrandoFormulario = false; limparCampos() }) {
                            Text("Cancelar", color = colors.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {

                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Planos", color = colors.onBackground, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                Text("Gerencia os planos da academia", color = colors.onSurfaceVariant, fontSize = 14.sp)
                            }
                            Button(
                                onClick = { mostrandoFormulario = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Color.Black)
                            ) {
                                Text("+ Novo Plano", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        LazyColumn {
                            items(planos) { plano ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text(plano.nome, color = colors.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                            Row {
                                                IconButton(onClick = {
                                                    idEditando = plano.id
                                                    nome = plano.nome
                                                    ativo = plano.ativo
                                                    dataFimNumeros = plano.dataFimPromocao.replace("-", "").let { if(it.length >= 8) it.substring(6,8) + it.substring(4,6) + it.substring(0,4) else "" }
                                                    horarioNumeros = plano.horarioLimiteAcesso.replace(":", "").take(4)

                                                    mostrandoFormulario = true
                                                }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = colors.onSurfaceVariant)
                                                }
                                                IconButton(onClick = { plano.id?.let { viewModel.deletarPlano(it) } }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Apagar", tint = colors.error)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Divider(color = colors.onSurfaceVariant.copy(alpha = 0.2f))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Column {
                                                Text("Validade", color = colors.onSurfaceVariant, fontSize = 12.sp)
                                                val dataExibicao = plano.dataFimPromocao.split("-").let {
                                                    if (it.size == 3) "${it[2]}/${it[1]}/${it[0]}" else plano.dataFimPromocao
                                                }
                                                Text(dataExibicao, color = colors.onBackground, fontWeight = FontWeight.SemiBold)
                                            }
                                            Column {
                                                Text("Horário Limite", color = colors.onSurfaceVariant, fontSize = 12.sp)
                                                val horaExibicao = plano.horarioLimiteAcesso.take(5)
                                                Text(horaExibicao, color = colors.onBackground, fontWeight = FontWeight.SemiBold)
                                            }
                                            Column {
                                                Text("Status", color = colors.onSurfaceVariant, fontSize = 12.sp)
                                                Text(if (plano.ativo) "Ativo" else "Inativo", color = if (plano.ativo) colors.primary else colors.error, fontWeight = FontWeight.SemiBold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun SeNumerosCompletos(texto: String, tamanhoEsperado: Int, transformacao: (String) -> String): String {
    return if (texto.length == tamanhoEsperado) transformacao(texto) else ""
}