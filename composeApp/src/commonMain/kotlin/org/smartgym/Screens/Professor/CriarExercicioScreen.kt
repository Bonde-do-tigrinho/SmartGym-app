package org.smartgym.Screens.Professor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.Font
import org.smartgym.model.professor.TipoExercicio
import org.smartgym.theme.SmartGymGreen
import org.smartgym.viewModel.Professor.ExerciciosViewModel
import smartgym.composeapp.generated.resources.Res
import smartgym.composeapp.generated.resources.inter_bold
import smartgym.composeapp.generated.resources.inter_regular
import smartgym.composeapp.generated.resources.inter_semibold

private val InterFont @Composable get() = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
    Font(Res.font.inter_bold, FontWeight.Bold)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarExercicioScreen(navController: NavController, viewModel: ExerciciosViewModel) {
    val nome by viewModel.nome.collectAsState()
    val descricao by viewModel.descricao.collectAsState()
    val tipo by viewModel.tipo.collectAsState()
    val grupoMuscular by viewModel.grupoMuscular.collectAsState()
    val editingId by viewModel.editingId.collectAsState()

    val isEditing = editingId != null
    val subtitleText = if (isEditing) "Editando exercício" else "Criando novo exercício"

    var grupoExpanded by remember { mutableStateOf(false) }
    var equipamentoExpanded by remember { mutableStateOf(false) }

    val equipamentos = listOf("Máquina", "Livre", "Halteres", "Polias", "Aerobicos")

    var equipamento by remember(tipo) {
        mutableStateOf(if (tipo == TipoExercicio.MAQUINA) "Máquina" else "Halteres")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // --- HEADER ---
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(start = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color(0xFF111827)
                )
            }

            Text(
                text = "Exercícios",
                fontFamily = InterFont,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color(0xFF111827)
            )

            Text(
                text = subtitleText,
                fontFamily = InterFont,
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- FORMULÁRIO (Rolável) ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FormLabel("Nome")
                    TextField(
                        value = nome,
                        onValueChange = viewModel::updateNome,
                        placeholder = { Text("Supino inclinado", fontFamily = InterFont, color = Color(0xFF9CA3AF)) },
                        leadingIcon = {
                            Icon(Icons.Outlined.FitnessCenter, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors(),
                        textStyle = LocalTextStyle.current.copy(fontFamily = InterFont) // <-- Fonte da digitação
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FormLabel("Descrição")
                    TextField(
                        value = descricao,
                        onValueChange = viewModel::updateDescricao,
                        placeholder = { Text("Exercício com foco no peitoral...", fontFamily = InterFont, color = Color(0xFF9CA3AF)) },
                        leadingIcon = {
                            Icon(Icons.Outlined.Assignment, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors(),
                        textStyle = LocalTextStyle.current.copy(fontFamily = InterFont) // <-- Fonte da digitação
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FormLabel("Grupo muscular")
                    ExposedDropdownMenuBox(
                        expanded = grupoExpanded,
                        onExpandedChange = { grupoExpanded = it }
                    ) {
                        TextField(
                            value = grupoMuscular,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Selecione...", fontFamily = InterFont, color = Color(0xFF9CA3AF)) },
                            leadingIcon = {
                                Icon(Icons.Outlined.AccessibilityNew, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = grupoExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors(),
                            textStyle = LocalTextStyle.current.copy(fontFamily = InterFont) // <-- Fonte do campo lido
                        )

                        ExposedDropdownMenu(
                            expanded = grupoExpanded,
                            onDismissRequest = { grupoExpanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            listOf("Peito", "Costas", "Pernas", "Ombros", "Biceps", "Triceps").forEach { opcao ->
                                DropdownMenuItem(
                                    text = { Text(opcao, fontFamily = InterFont) },
                                    onClick = {
                                        viewModel.updateGrupoMuscular(opcao)
                                        grupoExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FormLabel("Equipamento")
                    ExposedDropdownMenuBox(
                        expanded = equipamentoExpanded,
                        onExpandedChange = { equipamentoExpanded = it }
                    ) {
                        TextField(
                            value = equipamento,
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = {
                                Icon(Icons.Outlined.Build, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = equipamentoExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors(),
                            textStyle = LocalTextStyle.current.copy(fontFamily = InterFont) // <-- Fonte do campo lido
                        )

                        ExposedDropdownMenu(
                            expanded = equipamentoExpanded,
                            onDismissRequest = { equipamentoExpanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            equipamentos.forEach { opcao ->
                                DropdownMenuItem(
                                    text = { Text(opcao, fontFamily = InterFont) },
                                    onClick = {
                                        equipamento = opcao
                                        val novoTipo = when(opcao) {
                                            "Máquina" -> TipoExercicio.MAQUINA
                                            "Aerobicos" -> TipoExercicio.AEROBICO
                                            else -> TipoExercicio.LIVRE
                                        }
                                        viewModel.updateTipo(novoTipo)
                                        equipamentoExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // --- BOTÃO FIXO NO RODAPÉ ---
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (tipo == TipoExercicio.LIVRE || tipo == TipoExercicio.AEROBICO) {
                        viewModel.updateMaquinaId(null)
                    }
                    viewModel.save()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SmartGymGreen)
            ) {
                Text(
                    text = "Salvar",
                    color = Color.Black,
                    fontFamily = InterFont,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        fontFamily = InterFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        color = Color(0xFF111827),
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun fieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color(0xFFF9FAFB),
    unfocusedContainerColor = Color(0xFFF9FAFB),
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    focusedTextColor = Color(0xFF4B5563),
    unfocusedTextColor = Color(0xFF4B5563)
)