package org.smartgym.Screens.Professor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.Font
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
fun EditarFichaScreen(navController: NavController, viewModel: CriarFichaViewModel, fichaId: Int) {
    val exercicios by viewModel.exercicios.collectAsState()
    val nomeAluno by viewModel.nomeAluno.collectAsState()
    val vigencia by viewModel.vigencia.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val abaSelecionada by viewModel.abaSelecionada.collectAsState()
    val mapaDias by viewModel.mapaDias.collectAsState()
    val diaAtual = mapaDias[abaSelecionada]!!

    var exercicioExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

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

    // Dispara o carregamento puxando do banco os dados da ficha pelo ID
    LaunchedEffect(fichaId) {
        viewModel.loadInitialData()
        viewModel.loadById(fichaId)
        viewModel.navigationEvent.collectLatest { navController.popBackStack() }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 8.dp)) {

            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = MaterialTheme.colorScheme.onBackground)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Ficha de treino", fontFamily = InterFont, fontWeight = FontWeight.Bold, fontSize = 28.sp, color = MaterialTheme.colorScheme.onBackground)
            Text("Editando ficha de treino", fontFamily = InterFont, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // 1. Aluno (Leitura apenas na edição para manter a integridade)
                FormLabelFicha("Aluno (a)")
                TextField(
                    value = nomeAluno,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColorsFicha()
                )

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

                // 💡 INSERIDO: Seletor de Abas Fluidas para navegação entre os dias (A, B, C)
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

                // 3. Nome/Foco do Treino Dinâmico baseado na rotina selecionada
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

                // 4. Input para Buscar e Adicionar Novos Exercícios à rotina atual
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

                // 💡 INSERIDO: Lista de Cards de Exercícios Completos com Inputs Numéricos para a Edição
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

            // Botão Salvar Alterações da Ficha
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
                    Text("Salvar Alterações", color = Color.Black, fontFamily = InterFont, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

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
    }
}

@Composable
private fun compactFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
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