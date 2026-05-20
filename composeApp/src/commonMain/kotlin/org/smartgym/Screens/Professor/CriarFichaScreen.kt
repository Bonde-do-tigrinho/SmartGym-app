package org.smartgym.Screens.Professor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.Font
import org.smartgym.theme.SmartGymGreen
import org.smartgym.util.dateToEpochMillis
import org.smartgym.util.epochMillisToDateParts
import org.smartgym.util.formatDateDdMmYyyy
import org.smartgym.util.maskDateInput
import org.smartgym.util.parseDateDdMmYyyy
import org.smartgym.viewModel.Professor.CriarFichaViewModel
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
fun CriarFichaScreen(navController: NavController, viewModel: CriarFichaViewModel, fichaId: Long? = null) {
    val alunos by viewModel.alunos.collectAsState()
    val exercicios by viewModel.exercicios.collectAsState()
    val nomeAluno by viewModel.nomeAluno.collectAsState()
    val selectedAlunoId by viewModel.selectedAlunoId.collectAsState()
    val focoTreino by viewModel.focoTreino.collectAsState()
    val vigencia by viewModel.vigencia.collectAsState()
    val selectedExercicios by viewModel.selectedExercicios.collectAsState()
    val editingId by viewModel.editingId.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isEditing = fichaId != null || editingId != null

    var alunoExpanded by remember { mutableStateOf(false) }
    var exercicioExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var vigenciaInput by remember {
        mutableStateOf(TextFieldValue(text = vigencia, selection = TextRange(vigencia.length)))
    }

    LaunchedEffect(vigencia) {
        if (vigenciaInput.text != vigencia) {
            vigenciaInput = TextFieldValue(
                text = vigencia,
                selection = TextRange(vigencia.length)
            )
        }
    }

    val exerciciosSelecionados = selectedExercicios.mapNotNull { selecionado ->
        exercicios.firstOrNull { it.id == selecionado.exercicioId }?.let { exercicio ->
            selecionado to exercicio
        }
    }

    LaunchedEffect(fichaId) {
        if (!isEditing) {
            viewModel.clearForm()
        }
        viewModel.loadInitialData()
        if (fichaId != null) {
            viewModel.loadById(fichaId)
        }
        viewModel.navigationEvent.collectLatest {
            navController.popBackStack()
        }
    }

    val initialDateMillis = remember(vigencia) {
        parseDateDdMmYyyy(vigencia)?.let(::dateToEpochMillis)
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
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color(0xFF111827)
                )
            }

            Text(
                text = "Ficha de treino",
                fontFamily = InterFont,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color(0xFF111827)
            )

            Text(
                text = if (isEditing) "Editando ficha de treino" else "Criando nova ficha de treino",
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
                FormLabelFicha("Aluno(a)")
                ExposedDropdownMenuBox(
                    expanded = alunoExpanded,
                    onExpandedChange = { alunoExpanded = !alunoExpanded }
                ) {
                    TextField(
                        value = if (selectedAlunoId != null) nomeAluno else "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Selecione o aluno", fontFamily = InterFont, color = Color(0xFF9CA3AF)) },
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = Color(0xFF9CA3AF)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = alunoExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColorsFicha(),
                        textStyle = LocalTextStyle.current.copy(fontFamily = InterFont)
                    )

                    ExposedDropdownMenu(
                        expanded = alunoExpanded,
                        onDismissRequest = { alunoExpanded = false }
                    ) {
                        alunos.forEach { aluno ->
                            DropdownMenuItem(
                                text = { Text(aluno.nome, fontFamily = InterFont) },
                                onClick = {
                                    viewModel.updateSelectedAluno(aluno)
                                    alunoExpanded = false
                                }
                            )
                        }
                    }
                }

                FormLabelFicha("Foco do treino")
                TextField(
                    value = focoTreino,
                    onValueChange = viewModel::updateFocoTreino,
                    placeholder = { Text("Peito, costas, pernas...", fontFamily = InterFont, color = Color(0xFF9CA3AF)) },
                    leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Assignment, contentDescription = null, tint = Color(0xFF9CA3AF)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = fieldColorsFicha(),
                    textStyle = LocalTextStyle.current.copy(fontFamily = InterFont)
                )

                FormLabelFicha("Vigência")
                TextField(
                    value = vigenciaInput,
                    onValueChange = { newValue ->
                        val masked = maskDateInput(newValue.text)
                        viewModel.updateVigencia(masked)
                        vigenciaInput = TextFieldValue(
                            text = masked,
                            selection = TextRange(masked.length)
                        )
                    },
                    placeholder = { Text("Ex: 06/06/2026", fontFamily = InterFont, color = Color(0xFF9CA3AF)) },
                    leadingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Outlined.CalendarMonth, contentDescription = "Selecionar data", tint = Color(0xFF9CA3AF))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = fieldColorsFicha(),
                    textStyle = LocalTextStyle.current.copy(fontFamily = InterFont)
                )

                FormLabelFicha("Exercícios")
                ExposedDropdownMenuBox(
                    expanded = exercicioExpanded,
                    onExpandedChange = { exercicioExpanded = !exercicioExpanded }
                ) {
                    TextField(
                        value = "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Selecione os exercícios", fontFamily = InterFont, color = Color(0xFF9CA3AF)) },
                        leadingIcon = { Icon(Icons.Outlined.FitnessCenter, contentDescription = null, tint = Color(0xFF9CA3AF)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = exercicioExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColorsFicha(),
                        textStyle = LocalTextStyle.current.copy(fontFamily = InterFont)
                    )

                    ExposedDropdownMenu(
                        expanded = exercicioExpanded,
                        onDismissRequest = { exercicioExpanded = false }
                    ) {
                        exercicios.forEach { exercicio ->
                            DropdownMenuItem(
                                text = { Text(exercicio.nome, fontFamily = InterFont) },
                                onClick = {
                                    exercicio.id?.let(viewModel::addExercicio)
                                    exercicioExpanded = false
                                }
                            )
                        }
                    }
                }

                if (exerciciosSelecionados.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        exerciciosSelecionados.forEach { (config, exercicio) ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = SmartGymGreen.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = exercicio.nome,
                                            fontFamily = InterFont,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF111827),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            contentDescription = "Remover",
                                            tint = Color.Black,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable { exercicio.id?.let(viewModel::removeExercicio) }
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        TextField(
                                            value = config.series.toString().takeIf { config.series > 0 }.orEmpty(),
                                            onValueChange = { viewModel.updateSeries(config.exercicioId, it) },
                                            placeholder = { Text("Séries", fontFamily = InterFont, fontSize = 12.sp) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            singleLine = true,
                                            colors = fieldColorsFicha(),
                                            textStyle = LocalTextStyle.current.copy(fontFamily = InterFont, fontSize = 13.sp)
                                        )
                                        TextField(
                                            value = config.repeticoes.toString().takeIf { config.repeticoes > 0 }.orEmpty(),
                                            onValueChange = { viewModel.updateRepeticoes(config.exercicioId, it) },
                                            placeholder = { Text("Reps", fontFamily = InterFont, fontSize = 12.sp) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            singleLine = true,
                                            colors = fieldColorsFicha(),
                                            textStyle = LocalTextStyle.current.copy(fontFamily = InterFont, fontSize = 13.sp)
                                        )
                                        TextField(
                                            value = config.descansoSegundos.toString().takeIf { config.descansoSegundos > 0 }.orEmpty(),
                                            onValueChange = { viewModel.updateDescansoSegundos(config.exercicioId, it) },
                                            placeholder = { Text("Descanso", fontFamily = InterFont, fontSize = 12.sp) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            singleLine = true,
                                            colors = fieldColorsFicha(),
                                            textStyle = LocalTextStyle.current.copy(fontFamily = InterFont, fontSize = 13.sp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = viewModel::save,
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
                        text = if (isEditing) "Atualizar" else "Salvar",
                        color = Color.Black,
                        fontFamily = InterFont,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = initialDateMillis,
                initialDisplayMode = DisplayMode.Picker
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val selected = epochMillisToDateParts(millis)
                                val formatted = formatDateDdMmYyyy(selected)
                                viewModel.updateVigencia(formatted)
                                vigenciaInput = TextFieldValue(
                                    text = formatted,
                                    selection = TextRange(formatted.length)
                                )
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK", fontFamily = InterFont)
                    }
                },
                dismissButton = {
                    Button(onClick = { showDatePicker = false }) {
                        Text("Cancelar", fontFamily = InterFont)
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    title = null,
                    headline = null,
                    showModeToggle = false
                )
            }
        }
    }
}

@Composable
private fun FormLabelFicha(text: String) {
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
private fun fieldColorsFicha() = TextFieldDefaults.colors(
    focusedContainerColor = Color(0xFFF9FAFB),
    unfocusedContainerColor = Color(0xFFF9FAFB),
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    focusedTextColor = Color(0xFF4B5563),
    unfocusedTextColor = Color(0xFF4B5563)
)



