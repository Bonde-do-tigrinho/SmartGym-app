package org.smartgym.Screens.Professor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.smartgym.Screen
import org.smartgym.theme.SmartGymGreen
import org.smartgym.util.*
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
fun CriarFichaScreen(navController: NavController, viewModel: CriarFichaViewModel, fichaId: Int? = null) {
    val alunos by viewModel.alunos.collectAsState()
    val exercicios by viewModel.exercicios.collectAsState()
    val nomeAluno by viewModel.nomeAluno.collectAsState()
    val selectedAlunoId by viewModel.selectedAlunoId.collectAsState()
    val vigencia by viewModel.vigencia.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isEditing = fichaId != null
    val scope = rememberCoroutineScope()

    val abaSelecionada by viewModel.abaSelecionada.collectAsState()
    val mapaDias by viewModel.mapaDias.collectAsState()
    val diaAtual = mapaDias[abaSelecionada]!!

    var alunoExpanded by remember { mutableStateOf(false) }
    var exercicioExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showFichaExistenteDialog by remember { mutableStateOf(false) }
    var idFichaExistente by remember { mutableStateOf<Int?>(null) }

    var vigenciaInput by remember {
        mutableStateOf(TextFieldValue(text = vigencia, selection = TextRange(vigencia.length)))
    }

    LaunchedEffect(vigencia) {
        if (vigenciaInput.text != vigencia) {
            vigenciaInput = TextFieldValue(text = vigencia, selection = TextRange(vigencia.length))
        }
    }

    val exerciciosDoDiaVisual = diaAtual.exercicios.mapNotNull { selecionado ->
        exercicios.firstOrNull { it.id == selecionado.exercicioId }?.let { selecionado to it }
    }

    LaunchedEffect(fichaId) {
        viewModel.loadInitialData()
        if (fichaId != null) viewModel.loadById(fichaId) else viewModel.clearForm()
        viewModel.navigationEvent.collectLatest { navController.popBackStack() }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 8.dp)) {

            // Botão de voltar topo esquerdo
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = MaterialTheme.colorScheme.onBackground)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Ficha de treino", fontFamily = InterFont, fontWeight = FontWeight.Bold, fontSize = 28.sp, color = MaterialTheme.colorScheme.onBackground)
            Text(if (isEditing) "Editando ficha de treino" else "Criando nova ficha de treino", fontFamily = InterFont, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // 1. Dropdown de Alunos
                FormLabelFicha("Aluno (a)")
                ExposedDropdownMenuBox(
                    expanded = alunoExpanded,
                    onExpandedChange = { alunoExpanded = !alunoExpanded },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        value = if (selectedAlunoId != null) nomeAluno else "Selecione um aluno",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = alunoExpanded) },
                        colors = fieldColorsFicha(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = alunoExpanded,
                        onDismissRequest = { alunoExpanded = false }
                    ) {
                        alunos.forEach { aluno ->
                            DropdownMenuItem(
                                text = { Text(aluno.nome, fontFamily = InterFont) },
                                onClick = {
                                    alunoExpanded = false
                                    scope.launch {
                                        val idFicha = viewModel.obterIdFichaDoAluno(aluno.id)
                                        if (idFicha != null && !isEditing) {
                                            idFichaExistente = idFicha
                                            showFichaExistenteDialog = true
                                        } else {
                                            viewModel.updateSelectedAluno(aluno)
                                        }
                                    }
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

                // 2. Data de Vigência
                FormLabelFicha("Válido até...")
                TextField(
                    value = vigenciaInput,
                    onValueChange = { viewModel.updateVigencia(maskDateInput(it.text)) },
                    placeholder = { Text("06/06/2026", fontSize = 15.sp) },
                    leadingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Outlined.CalendarMonth, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColorsFicha()
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                // 💡 REINSERÇÃO: Seletor Fluido de Abas (Treino A, B, C)
                FormLabelFicha("Configuração da Rotina")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Treino A", "Treino B", "Treino C").forEachIndexed { index, label ->
                        val selected = abaSelecionada == index
                        Button(
                            onClick = { viewModel.selecionarAba(index) },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) SmartGymGreen else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                contentColor = if (selected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = InterFont)
                        }
                    }
                }

                // 3. Campo de Foco do Dia Dinâmico (Muda baseado na aba selecionada)
                FormLabelFicha("Foco do Treino ${diaAtual.letra}")
                TextField(
                    value = diaAtual.focoTreino,
                    onValueChange = { viewModel.updateFocoDoDia(it) },
                    placeholder = { Text("Ex: Peito e Tríceps", fontSize = 15.sp) },
                    leadingIcon = { Icon(Icons.Outlined.FitnessCenter, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColorsFicha()
                )

                // 4. Campo Adicionar Exercícios ao Dia Selecionado
                FormLabelFicha("Adicionar Exercício ao Treino ${diaAtual.letra}")
                ExposedDropdownMenuBox(expanded = exercicioExpanded, onExpandedChange = { exercicioExpanded = !exercicioExpanded }) {
                    TextField(
                        value = "", onValueChange = {}, readOnly = true,
                        placeholder = { Text("Buscar e adicionar exercício...", fontSize = 15.sp) },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(10.dp),
                        colors = fieldColorsFicha()
                    )
                    ExposedDropdownMenu(expanded = exercicioExpanded, onDismissRequest = { exercicioExpanded = false }) {
                        exercicios.forEach { exercicio ->
                            DropdownMenuItem(text = { Text(exercicio.nome) }, onClick = {
                                exercicio.id?.let { viewModel.addExercicio(it) }
                                exercicioExpanded = false
                            })
                        }
                    }
                }

                // 💡 REINSERÇÃO: Lista de Exercícios Configuráveis com os Inputs de Séries, Reps e Segundos
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    exerciciosDoDiaVisual.forEach { (config, exercicio) ->
                        ExercicioEditCardReal(
                            nome = exercicio.nome,
                            config = config,
                            onUpdateSeries = { viewModel.updateSeries(config.exercicioId, it) },
                            onUpdateReps = { viewModel.updateRepeticoes(config.exercicioId, it) },
                            onUpdateRest = { viewModel.updateDescansoSegundos(config.exercicioId, it) },
                            onRemove = { viewModel.removeExercicio(config.exercicioId) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Salvar Estilo Mockup (Verde Limão/Tema do App)
            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SmartGymGreen),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                } else {
                    Text("Salvar Ficha Completa", color = Color.Black, fontFamily = InterFont, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Seletor de Calendário Nativo
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            viewModel.updateVigencia(formatDateDdMmYyyy(epochMillisToDateParts(it)))
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                }
            ) { DatePicker(state = datePickerState, title = null, headline = null, showModeToggle = false) }
        }

        // Alerta de Ficha Ativa Existente
        if (showFichaExistenteDialog && idFichaExistente != null) {
            Dialog(onDismissRequest = { showFichaExistenteDialog = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Info, "Aviso", tint = SmartGymGreen, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Aluno já possui ficha!", fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = InterFont, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Para evitar conflitos de treino, recomendamos que você altere a ficha existente em vez de criar uma nova.", fontSize = 14.sp, fontFamily = InterFont, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    showFichaExistenteDialog = false
                                    viewModel.prepareForEdit(idFichaExistente!!)
                                    navController.navigate("${Screen.EditarFicha.route}/$idFichaExistente") {
                                        popUpTo(Screen.NovaFicha.route) { inclusive = true }
                                    }
                                },
                                modifier = Modifier.weight(1.5f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SmartGymGreen)
                            ) {
                                Text("Alterar Ficha", color = Color.Black, fontFamily = InterFont, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { showFichaExistenteDialog = false },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                            ) {
                                Text("Cancelar", fontFamily = InterFont)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExercicioEditCardReal(
    nome: String,
    config: org.smartgym.model.professor.ExercicioFichaTreino,
    onUpdateSeries: (String) -> Unit,
    onUpdateReps: (String) -> Unit,
    onUpdateRest: (String) -> Unit,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(SmartGymGreen.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(nome, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontFamily = InterFont)
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Outlined.Close, "Remover", modifier = Modifier.size(18.dp), tint = Color.Red)
            }
        }

        // Linha com as 3 entradas numéricas usando BasicTextField (Livre de paddings invisíveis)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val fieldModifier = Modifier.weight(1f).height(44.dp) // Pode até manter 44dp bem compacto que não corta!

            CompactNumericInputField(
                value = config.series.let { if (it > 0) it.toString() else "" },
                onValueChange = onUpdateSeries,
                placeholder = "Séries",
                modifier = fieldModifier
            )
            CompactNumericInputField(
                value = config.repeticoes.let { if (it > 0) it.toString() else "" },
                onValueChange = onUpdateReps,
                placeholder = "Reps",
                modifier = fieldModifier
            )
            CompactNumericInputField(
                value = config.descansoSegundos.let { if (it > 0) it.toString() else "" },
                onValueChange = onUpdateRest,
                placeholder = "Descanso",
                modifier = fieldModifier
            )
        }
    }
}

// 🎯 Componente Auxiliar Customizado de Alta Performance Visual
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactNumericInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    val textStyle = LocalTextStyle.current.copy(
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface,
        fontFamily = InterFont
    )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = textStyle,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                placeholder = {
                    Text(
                        text = placeholder,
                        fontSize = 12.sp,
                        fontFamily = InterFont,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    )
}

@Composable
private fun compactFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
)

@Composable
private fun FormLabelFicha(text: String) = Text(
    text = text,
    fontFamily = InterFont,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    color = MaterialTheme.colorScheme.onBackground,
    modifier = Modifier.padding(start = 2.dp)
)

@Composable
private fun fieldColorsFicha() = TextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
)

@Composable
fun InputChipReal(
    text: String,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = SmartGymGreen.copy(alpha = 0.25f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onRemove() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Remover",
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = text,
                fontFamily = InterFont,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}