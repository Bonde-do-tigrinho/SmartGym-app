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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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

data class Ficha(
    val id: String,
    val nomeAluno: String,
    val nomeTreino: String,
    val dataCriacao: String,
    val dataValidade: String,
    val numExercicios: Int
)

@Composable
fun FichasScreen(navController: NavController) {
    val searchText = remember { mutableStateOf("") }
    val showMenu = remember { mutableStateOf(false) }

    val fichas = listOf(
        Ficha(
            id = "1",
            nomeAluno = "Lucas Mendes",
            nomeTreino = "Treino A - Peito e Tríceps",
            dataCriacao = "15/03/2026",
            dataValidade = "15/06/2026",
            numExercicios = 3
        ),
        Ficha(
            id = "2",
            nomeAluno = "Fernanda Lima",
            nomeTreino = "Treino B - Costas e Bíceps",
            dataCriacao = "14/03/2026",
            dataValidade = "14/06/2026",
            numExercicios = 2
        ),
        Ficha(
            id = "3",
            nomeAluno = "João Silva",
            nomeTreino = "Treino C - Pernas",
            dataCriacao = "10/03/2026",
            dataValidade = "10/06/2026",
            numExercicios = 4
        ),
        Ficha(
            id = "4",
            nomeAluno = "Maria Santos",
            nomeTreino = "Treino D - Full Body",
            dataCriacao = "09/03/2026",
            dataValidade = "09/06/2026",
            numExercicios = 5
        )
    )

    val fichasFiltradas = fichas.filter {
        it.nomeAluno.contains(searchText.value, ignoreCase = true)
    }

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
                    .padding(16.dp)
            ) {
                Text(
                    "Fichas de Treino",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = InterFont
                )

                Text(
                    "Gerencie as fichas dos alunos",
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
                        "+ Nova Ficha",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        fontFamily = InterFont
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = searchText.value,
                    onValueChange = { searchText.value = it },
                    placeholder = {
                        Text(
                            "Buscar fichas...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = InterFont
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(vertical = 5.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
                        fontSize = 13.sp,
                        fontFamily = InterFont
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ✅ LAZYCOLUMN - Lista de fichas
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(fichasFiltradas.size) { index ->
                        FichaItem(ficha = fichasFiltradas[index])
                    }
                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }

    }
}

@Composable
fun FichaItem(ficha: Ficha) {
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
            // Nome do Aluno - Destaque
            Text(
                ficha.nomeAluno,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = InterFont
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Nome do Treino - Badge
            Box(
                modifier = Modifier
                    .background(
                        color = SmartGymGreen,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    ficha.nomeTreino,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    fontFamily = InterFont
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Separador
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Informações
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                InfoRow(
                    label = "Criado em:",
                    value = ficha.dataCriacao
                )
                InfoRow(
                    label = "Válido até:",
                    value = ficha.dataValidade
                )
                InfoRow(
                    label = "Exercícios:",
                    value = ficha.numExercicios.toString()
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

            // Botões
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "👁 Visualizar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = InterFont
                    )
                }

                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "✏ Editar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = InterFont
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = InterFont
        )
        Text(
            value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = InterFont
        )
    }
}