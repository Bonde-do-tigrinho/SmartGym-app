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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.Font
import org.smartgym.theme.SmartGymGreen
import org.smartgym.viewModel.Professor.AvaliacoesViewModel
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
fun CriarAvaliacaoScreen(navController: NavController, viewModel: AvaliacoesViewModel) {
    val nomeAluno by viewModel.nomeAluno.collectAsState()
    val alunosResumo by viewModel.alunosResumo.collectAsState()
    val selectedAlunoId by viewModel.selectedAlunoId.collectAsState()
    val dataAvaliacao by viewModel.dataAvaliacao.collectAsState()
    val peso by viewModel.peso.collectAsState()
    val percentualGordura by viewModel.percentualGordura.collectAsState()
    val imc by viewModel.imc.collectAsState()
    val nota by viewModel.nota.collectAsState()
    val editingId by viewModel.editingId.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var alunoDropdownExpanded by remember { mutableStateOf(false) }

    val isEditing = editingId != null

    LaunchedEffect(Unit) {
        viewModel.loadAlunosResumo()
        viewModel.navigationEvent.collectLatest {
            navController.popBackStack()
        }
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
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(start = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color(0xFF111827)
                )
            }

            Text(
                text = "Avaliacoes",
                fontFamily = InterFont,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color(0xFF111827)
            )

            Text(
                text = if (isEditing) "Editando avaliacao" else "Criando nova avaliacao",
                fontFamily = InterFont,
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FormLabelAvaliacao("Aluno")
                ExposedDropdownMenuBox(
                    expanded = alunoDropdownExpanded,
                    onExpandedChange = { alunoDropdownExpanded = !alunoDropdownExpanded }
                ) {
                    TextField(
                        value = if (selectedAlunoId != null && nomeAluno.isNotBlank()) "$nomeAluno (ID: $selectedAlunoId)" else "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Selecione o aluno", fontFamily = InterFont, color = Color(0xFF9CA3AF)) },
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = Color(0xFF9CA3AF)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = alunoDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColorsAvaliacao(),
                        textStyle = LocalTextStyle.current.copy(fontFamily = InterFont)
                    )

                    ExposedDropdownMenu(
                        expanded = alunoDropdownExpanded,
                        onDismissRequest = { alunoDropdownExpanded = false }
                    ) {
                        alunosResumo.forEach { aluno ->
                            DropdownMenuItem(
                                text = { Text("${aluno.nome} (ID: ${aluno.id})", fontFamily = InterFont) },
                                onClick = {
                                    viewModel.updateSelectedAlunoId(aluno.id)
                                    alunoDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                FormLabelAvaliacao("Data da avaliacao")
                TextField(
                    value = dataAvaliacao,
                    onValueChange = viewModel::updateDataAvaliacao,
                    placeholder = { Text("Ex: 2026-04-16", fontFamily = InterFont, color = Color(0xFF9CA3AF)) },
                    leadingIcon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = Color(0xFF9CA3AF)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = fieldColorsAvaliacao(),
                    textStyle = LocalTextStyle.current.copy(fontFamily = InterFont)
                )

                FormLabelAvaliacao("Peso")
                TextField(
                    value = peso,
                    onValueChange = viewModel::updatePeso,
                    placeholder = { Text("Ex: 78.5 kg", fontFamily = InterFont, color = Color(0xFF9CA3AF)) },
                    leadingIcon = { Icon(Icons.Outlined.MonitorWeight, contentDescription = null, tint = Color(0xFF9CA3AF)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = fieldColorsAvaliacao(),
                    textStyle = LocalTextStyle.current.copy(fontFamily = InterFont)
                )

                FormLabelAvaliacao("Percentual de gordura")
                TextField(
                    value = percentualGordura,
                    onValueChange = viewModel::updatePercentualGordura,
                    placeholder = { Text("Ex: 15.2%", fontFamily = InterFont, color = Color(0xFF9CA3AF)) },
                    leadingIcon = { Icon(Icons.Outlined.Percent, contentDescription = null, tint = Color(0xFF9CA3AF)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = fieldColorsAvaliacao(),
                    textStyle = LocalTextStyle.current.copy(fontFamily = InterFont)
                )

                FormLabelAvaliacao("IMC")
                TextField(
                    value = imc,
                    onValueChange = viewModel::updateImc,
                    placeholder = { Text("Ex: 25.6", fontFamily = InterFont, color = Color(0xFF9CA3AF)) },
                    leadingIcon = { Icon(Icons.Outlined.FitnessCenter, contentDescription = null, tint = Color(0xFF9CA3AF)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = fieldColorsAvaliacao(),
                    textStyle = LocalTextStyle.current.copy(fontFamily = InterFont)
                )

                FormLabelAvaliacao("Observacoes")
                TextField(
                    value = nota,
                    onValueChange = viewModel::updateNota,
                    placeholder = { Text("Observacoes do professor", fontFamily = InterFont, color = Color(0xFF9CA3AF)) },
                    leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Assignment, contentDescription = null, tint = Color(0xFF9CA3AF)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColorsAvaliacao(),
                    textStyle = LocalTextStyle.current.copy(fontFamily = InterFont)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.save()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SmartGymGreen),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.Black)
                } else {
                    Text(
                        text = "Salvar",
                        color = Color.Black,
                        fontFamily = InterFont,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FormLabelAvaliacao(text: String) {
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
private fun fieldColorsAvaliacao() = TextFieldDefaults.colors(
    focusedContainerColor = Color(0xFFF9FAFB),
    unfocusedContainerColor = Color(0xFFF9FAFB),
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    focusedTextColor = Color(0xFF4B5563),
    unfocusedTextColor = Color(0xFF4B5563)
)

