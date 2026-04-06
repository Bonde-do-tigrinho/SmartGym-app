package org.smartgym.Screens.Aluno

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.Font
import smartgym.composeapp.generated.resources.Res
import smartgym.composeapp.generated.resources.inter_bold
import smartgym.composeapp.generated.resources.inter_regular
import smartgym.composeapp.generated.resources.inter_semibold

data class PlanInfo(
    val nome: String,
    val preco: String,
    val icone: String,
    val beneficios: List<String>
)

private val InterFont @Composable get() = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
    Font(Res.font.inter_bold, FontWeight.Bold)
)

@Composable
fun PagamentosScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Botão de voltar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.popBackStack() }
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            // ...existing code...
            // Cabeçalho
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "PAGAMENTOS.",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = InterFont
                )
                Text(
                    "Seu plano e histórico",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = InterFont
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card do Plano Ativo
            val isDarkMode = isSystemInDarkTheme()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isDarkMode) {
                            // Dark Mode: Gradiente
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF3A3A2A),
                                    Color(0xFF39392A),
                                    Color(0xFF37372A),
                                    Color(0xFF36362A),
                                    Color(0xFF35352A),
                                    Color(0xFF33332A),
                                    Color(0xFF32322A),
                                    Color(0xFF31312A),
                                    Color(0xFF2F2F2A),
                                    Color(0xFF2E2E2A),
                                    Color(0xFF2D2D2A),
                                    Color(0xFF2B2B2A),
                                    MaterialTheme.colorScheme.surface
                                ),
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(500f, 500f)
                            )
                        } else {
                            // Light Mode: Cor sólida branca
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "PLANO ATUAL",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = InterFont
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "PREMIUM",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontFamily = InterFont
                            )
                        }
                        Text(
                            "R$149,90",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = InterFont
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Próximo vencimento",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = InterFont
                    )
                    Text(
                        "15/04/2026",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = InterFont
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Seção Planos Disponíveis
            Text(
                "PLANOS DISPONÍVEIS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = InterFont
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Carrossel de Planos
            val planos = listOf(
                PlanInfo(
                    nome = "BASIC",
                    preco = "R$89,90",
                    icone = "B",
                    beneficios = listOf(
                        "Acesso à musculação",
                        "Horário limitado: 6h-17h",
                        "1 avaliação física/mês"
                    )
                ),
                PlanInfo(
                    nome = "PREMIUM",
                    preco = "R$149,90",
                    icone = "P",
                    beneficios = listOf(
                        "Acesso ilimitado",
                        "Horário 24 horas",
                        "Acompanhamento de treino",
                        "1 avaliação física/mês"
                    )
                )
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(planos) { plano ->
                    PlanoCard(plano = plano)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Seção Histórico
            Text(
                "HISTÓRICO",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = InterFont
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Item Histórico 1
            HistoricoItem(
                mes = "Mensalidade Janeiro",
                valor = "R$149,90",
                data = "15/01/2026",
                status = "Pago",
                isPago = true
            )

            // Item Histórico 2
            HistoricoItem(
                mes = "Mensalidade Fevereiro",
                valor = "R$149,90",
                data = "15/02/2026",
                status = "Pago",
                isPago = true
            )

            // Item Histórico 3
            HistoricoItem(
                mes = "Mensalidade Março",
                valor = "R$149,90",
                data = "15/03/2026",
                status = "Pago",
                isPago = true
            )

            // Item Histórico 4
            HistoricoItem(
                mes = "Mensalidade Abril",
                valor = "R$149,90",
                data = "15/04/2026",
                status = "Pendente",
                isPago = false
            )

            // Spacer grande para espaço abaixo da barra de navegação
            Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

@Composable
fun PlanoCard(plano: PlanInfo) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(250.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        plano.nome,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = InterFont
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        plano.preco,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = InterFont
                    )
                    Text(
                        "/mês",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = InterFont
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        plano.icone,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontFamily = InterFont
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            plano.beneficios.forEach { beneficio ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "✓",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        beneficio,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = InterFont
                    )
                }
            }
        }
    }
}

@Composable
fun HistoricoItem(
    mes: String,
    valor: String,
    status: String,
    data: String = "",
    isPago: Boolean = true
) {
    // Cores baseadas no status
    val (backgroundColor, iconColor, textColor) = if (isPago) {
        Triple(
            Color(0xFF1B4D2F),  // Verde escuro
            Color(0xFF2ECC71),  // Verde brilhante
            Color(0xFF2ECC71)   // Verde texto
        )
    } else {
        Triple(
            Color(0xFF4D3F1B),  // Amarelo/laranja escuro
            Color(0xFFF39C12),  // Laranja
            Color(0xFFF39C12)   // Laranja texto
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone visual
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = iconColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (isPago) "✓" else "!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Informações do pagamento
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    mes,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (data.isNotEmpty()) {
                    Text(
                        data,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Valor e Status
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    valor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    status,
                    fontSize = 12.sp,
                    color = textColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
