package org.smartgym.Screens.Professor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import org.smartgym.util.formatDateToUi
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

@Composable
fun VisualizarFichaScreen(navController: NavController, viewModel: CriarFichaViewModel, fichaId: Int) {
    val exercicios by viewModel.exercicios.collectAsState()
    val nomeAluno by viewModel.nomeAluno.collectAsState()
    val vigencia by viewModel.vigencia.collectAsState()

    val abaSelecionada by viewModel.abaSelecionada.collectAsState()
    val mapaDias by viewModel.mapaDias.collectAsState()
    val diaAtual = mapaDias[abaSelecionada]!!

    val exerciciosDoDiaVisual = diaAtual.exercicios.mapNotNull { selecionado ->
        exercicios.firstOrNull { it.id == selecionado.exercicioId }?.let { selecionado to it }
    }

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
            Text("Visualizando detalhes da periodização", fontFamily = InterFont, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                FormLabelFicha("Aluno (a)")
                TextField(
                    value = nomeAluno,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = disabledFieldColors()
                )

                FormLabelFicha("Válido até...")
                TextField(
                    value = formatDateToUi(vigencia).ifBlank { vigencia },
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Outlined.CalendarMonth, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = disabledFieldColors()
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                // 💡 INSERIDO: Seletor de Abas Fluidas para navegar entre Treino A, B e C
                FormLabelFicha("Visualizar Rotina")
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

                // Nome do Treino Dinâmico (Foco do dia selecionado)
                FormLabelFicha("Foco do Treino ${diaAtual.letra}")
                TextField(
                    value = diaAtual.focoTreino.ifBlank { "Nenhum foco cadastrado" },
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Outlined.FitnessCenter, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = disabledFieldColors()
                )

                FormLabelFicha("Exercícios do Treino ${diaAtual.letra}")

                if (exerciciosDoDiaVisual.isEmpty()) {
                    Text(
                        text = "Nenhum exercício registrado para este dia.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 2.dp, top = 4.dp),
                        fontFamily = InterFont
                    )
                } else {
                    exerciciosDoDiaVisual.forEach { (config, exercicio) ->
                        val grupoReal = viewModel.obterGrupoMuscular(config.exercicioId)

                        ExercicioVisualizarCard(
                            nome = exercicio.nome,
                            grupoMuscular = grupoReal.ifBlank { "Geral" },
                            descricao = "Séries: ${config.series}  |  Repetições: ${config.repeticoes}  |  Descanso: ${config.descansoSegundos}s"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SmartGymGreen)
            ) {
                Text("Voltar", color = Color.Black, fontFamily = InterFont, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ExercicioVisualizarCard(
    nome: String,
    grupoMuscular: String,
    descricao: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = nome,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = InterFont
            )

            val corDaTag = mapearCorGrupoMuscular(grupoMuscular)
            Box(
                modifier = Modifier
                    .background(color = corDaTag.copy(alpha = 0.15f), shape = CircleShape)
                    .border(1.dp, corDaTag.copy(alpha = 0.4f), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = grupoMuscular,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = corDaTag,
                    fontFamily = InterFont
                )
            }

            Text(
                text = descricao,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = InterFont,
                lineHeight = 20.sp
            )

            Text(
                text = "Equipamento: Ajustável / Máquina",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontFamily = InterFont
            )
        }
    }
}

@Composable
fun mapearCorGrupoMuscular(grupo: String): Color {
    val g = grupo.lowercase()
    return when {
        g.contains("peito") || g.contains("superior") || g.contains("ombro") -> Color(0xFFEF4444)
        g.contains("triceps") || g.contains("biceps") || g.contains("braço") || g.contains("costas") -> Color(0xFF84CC16)
        g.contains("perna") || g.contains("coxa") || g.contains("panturrilha") || g.contains("inferior") -> Color(0xFF3B82F6)
        else -> SmartGymGreen
    }
}

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
private fun disabledFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    focusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
)