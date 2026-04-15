package org.smartgym.Screens.Professor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.Font
import org.smartgym.theme.SmartGymGreen
import smartgym.composeapp.generated.resources.Res
import smartgym.composeapp.generated.resources.inter_bold
import smartgym.composeapp.generated.resources.inter_regular
import smartgym.composeapp.generated.resources.inter_semibold

private val InterFont @Composable get() = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
    Font(Res.font.inter_bold, FontWeight.Bold)
)

data class Avaliacao(
    val id: Int,
    val nomeAluno: String,
    val dataAvaliacao: String,
    val peso: String,
    val percentualGordura: String,
    val imc: String,
    val nota: String
)

@Composable
fun AvaliacoesScreen(navController: NavController) {
    val searchQuery = remember { mutableStateOf("") }
    val showMenu = remember { mutableStateOf(false) }

    val avaliacoes = listOf(
        Avaliacao(
            id = 1,
            nomeAluno = "Lucas Mendes",
            dataAvaliacao = "15/03/2026",
            peso = "78.5 kg",
            percentualGordura = "15.2%",
            imc = "25.6",
            nota = "Boa evolução. Manter treino atual."
        ),
        Avaliacao(
            id = 2,
            nomeAluno = "Fernanda Lima",
            dataAvaliacao = "14/03/2026",
            peso = "62 kg",
            percentualGordura = "22.5%",
            imc = "22.8",
            nota = "Iniciar treino de força."
        ),
        Avaliacao(
            id = 3,
            nomeAluno = "João Silva",
            dataAvaliacao = "10/03/2026",
            peso = "85 kg",
            percentualGordura = "18.5%",
            imc = "28.1",
            nota = "Reduzir percentual de gordura."
        ),
        Avaliacao(
            id = 4,
            nomeAluno = "Maria Santos",
            dataAvaliacao = "09/03/2026",
            peso = "55 kg",
            percentualGordura = "20.1%",
            imc = "21.3",
            nota = "Ótimo progresso no programa."
        ),
        Avaliacao(
            id = 5,
            nomeAluno = "Pedro Costa",
            dataAvaliacao = "08/03/2026",
            peso = "92 kg",
            percentualGordura = "26.3%",
            imc = "30.2",
            nota = "Aumentar intensidade do treino."
        )
    )

    val avaliacoesFiltradas = avaliacoes.filter {
        it.nomeAluno.contains(searchQuery.value, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ProfessorHeader(
                onMenuClick = { showMenu.value = !showMenu.value }
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    "Avaliações Físicas",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = InterFont
                )

                Text(
                    "evolução dos alunos",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = InterFont
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SmartGymGreen
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "+ Nova Avaliação",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        fontFamily = InterFont
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    placeholder = {
                        Text(
                            "Buscar avaliações...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = InterFont,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(vertical = 5.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                    ),
                    singleLine = true,
                    textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
                        fontSize = 13.sp,
                        fontFamily = InterFont
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ✅ LAZYCOLUMN - Lista os cards de avaliações
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(avaliacoesFiltradas.size) { index ->
                        AvaliacaoCard(avaliacao = avaliacoesFiltradas[index])
                    }
                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }

        ProfessorMenuOverlay(
            showMenu = showMenu.value,
            onDismiss = { showMenu.value = false },
            navController = navController
        )
    }
}

@Composable
fun AvaliacaoCard(avaliacao: Avaliacao) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Nome do Aluno - Destaque Principal
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        avaliacao.nomeAluno,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = InterFont
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        avaliacao.dataAvaliacao,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = InterFont
                    )
                }

                IconButton(
                    onClick = {},
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Visualizar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Separador
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Métricas - Destacadas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricaAvaliacao(
                    label = "Peso",
                    valor = avaliacao.peso,
                    modifier = Modifier.weight(1f)
                )

                MetricaAvaliacao(
                    label = "% Gordura",
                    valor = avaliacao.percentualGordura,
                    modifier = Modifier.weight(1f)
                )

                MetricaAvaliacao(
                    label = "IMC",
                    valor = avaliacao.imc,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Separador
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Nota do Professor
            Text(
                "\"${avaliacao.nota}\"",
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = InterFont
            )
        }
    }
}

@Composable
fun MetricaAvaliacao(
    label: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = InterFont
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            valor,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = InterFont
        )
    }
}