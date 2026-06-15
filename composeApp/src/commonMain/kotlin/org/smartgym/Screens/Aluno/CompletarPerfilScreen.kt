package org.smartgym.Screens.Aluno

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.smartgym.viewModel.aluno.CompletarPerfilViewModel
import org.smartgym.viewModel.aluno.CompletarPerfilState
import org.smartgym.viewModel.aluno.PlanoDto
import org.smartgym.viewModel.aluno.ProfessorDto

@Composable
fun CompletarPerfilScreen(
    navController: NavController,
    viewModel: CompletarPerfilViewModel
) {
    val planos by viewModel.planos.collectAsState()
    val professores by viewModel.professores.collectAsState()
    val planoSelecionado by viewModel.planoSelecionado.collectAsState()
    val professorSelecionado by viewModel.professorSelecionado.collectAsState()
    val state by viewModel.state.collectAsState()
    val colors = MaterialTheme.colorScheme

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            Text("Complete seu", color = colors.onSurfaceVariant, fontSize = 16.sp)
            Text("CADASTRO", color = colors.onBackground, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Text("Escolha seu plano e professor para começar!", color = colors.onSurfaceVariant, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
        }

        item {
            Text("Escolha seu Plano", color = colors.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        items(planos) { plano ->
            CardPlano(
                plano = plano,
                selecionado = planoSelecionado?.id == plano.id,
                onClick = { viewModel.planoSelecionado.value = plano }
            )
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text("Escolha seu Professor", color = colors.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        items(professores) { professor ->
            CardProfessor(
                professor = professor,
                selecionado = professorSelecionado?.id == professor.id,
                onClick = { viewModel.professorSelecionado.value = professor }
            )
        }
        if (state is CompletarPerfilState.Error) {
            item {
                Text(
                    text = (state as CompletarPerfilState.Error).message,
                    color = colors.error,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.completarPerfil {
                        navController.navigate("home_aluno") {
                            popUpTo("completar-perfil") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = state !is CompletarPerfilState.Loading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                if (state is CompletarPerfilState.Loading) {
                    CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Concluir Matrícula", color = colors.onPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun CardPlano(plano: PlanoDto, selecionado: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (selecionado) 2.dp else 1.dp,
                color = if (selecionado) colors.primary else colors.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selecionado) colors.primary.copy(alpha = 0.1f) else colors.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(plano.nome, color = colors.onSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                plano.descricao?.let { Text(it, color = colors.onSurfaceVariant, fontSize = 13.sp) }
                Text("${plano.duracaoMeses} mês(es)", color = colors.onSurfaceVariant, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = plano.valor.toCurrency(),
                    color = colors.primary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                Text("/mês", color = colors.onSurfaceVariant, fontSize = 12.sp)
            }
            if (selecionado) {
                Spacer(Modifier.width(12.dp))
                Icon(Icons.Default.Check, contentDescription = null, tint = colors.primary)
            }
        }
    }
}

@Composable
private fun CardProfessor(professor: ProfessorDto, selecionado: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (selecionado) 2.dp else 1.dp,
                color = if (selecionado) colors.primary else colors.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selecionado) colors.primary.copy(alpha = 0.1f) else colors.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(colors.surfaceVariant, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = professor.nome.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                    color = colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(professor.nome, color = colors.onSurface, fontWeight = FontWeight.Bold)
                Text(professor.email, color = colors.onSurfaceVariant, fontSize = 13.sp)
            }
            if (selecionado) {
                Icon(Icons.Default.Check, contentDescription = null, tint = colors.primary)
            }
        }
    }
}

fun Double.toCurrency(): String {
    val parts = this.toString().split(".")
    val inteiro = parts[0]
    var decimal = if (parts.size > 1) parts[1] else "00"
    if (decimal.length == 1) decimal += "0"
    return "R$ $inteiro,${decimal.take(2)}"
}