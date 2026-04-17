package org.smartgym.Screens.Professor

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun HomeProfessorScreen(navController: NavController) {
    val showMenu = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Título e Boas-vindas
                Text(
                    "Dashboard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = InterFont
                )

                Text(
                    "Bem-vindo de volta, Rafael Silva",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = InterFont
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatisticCard(
                        numero = "45",
                        label = "Alunos Ativos",
                        backgroundColor = Color(0xFF2563EB),
                        modifier = Modifier.weight(1f)
                    )

                    StatisticCard(
                        numero = "38",
                        label = "Fichas de Treino",
                        backgroundColor = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatisticCard(
                        numero = "7",
                        label = "Avaliações Pendentes",
                        backgroundColor = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )

                    StatisticCard(
                        numero = "124",
                        label = "Exercícios Cadastrados",
                        backgroundColor = Color(0xFFA855F7),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Atividades Recentes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = InterFont
                )

                Spacer(modifier = Modifier.height(16.dp))

                AtividadeItem(
                    titulo = "Ficha atualizada",
                    descricao = "Lucas Mendes - Treino A",
                    data = "Há 2 horas"
                )

                AtividadeItem(
                    titulo = "Avaliação física realizada",
                    descricao = "Fernanda Lima - Avaliação inicial",
                    data = "Há 4 horas"
                )

                AtividadeItem(
                    titulo = "Novo exercício cadastrado",
                    descricao = "Supino",
                    data = "Ontem"
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Ações Rápidas
                Text(
                    "Ações Rápidas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = InterFont
                )

                Spacer(modifier = Modifier.height(16.dp))

                BotaoAcaoRapida(
                    texto = "+ Nova Ficha de Treino",
                    backgroundColor = SmartGymGreen,
                    textColor = Color.Black,
                    onClick = {}
                )

                BotaoAcaoRapida(
                    texto = "+ Nova Avaliação Física",
                    backgroundColor = Color(0xFF1F2937),
                    textColor = Color.White,
                    onClick = {}
                )

                BotaoAcaoRapida(
                    texto = "+ Novo Exercício",
                    backgroundColor = Color(0xFFF3F4F6),
                    textColor = Color.Black,
                    onClick = {}
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

    }
}

@Composable
fun StatisticCard(
    numero: String,
    label: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "📊",
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                numero,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = InterFont
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                label,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = InterFont
            )
        }
    }
}

@Composable
fun AtividadeItem(
    titulo: String,
    descricao: String,
    data: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(SmartGymGreen, shape = RoundedCornerShape(50))
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = InterFont
            )

            Text(
                descricao,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = InterFont
            )
        }

        Text(
            data,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = InterFont
        )
    }
}

@Composable
fun BotaoAcaoRapida(
    texto: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            texto,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            fontFamily = InterFont
        )
    }

    Spacer(modifier = Modifier.height(12.dp))
}