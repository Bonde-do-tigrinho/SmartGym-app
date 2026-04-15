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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
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

data class Exercicio(
    val id: Int,
    val nome: String,
    val grupoMuscular: String,
    val corGrupo: Color,
    val descricao: String,
    val equipamento: String
)

@Composable
fun ExerciciosScreen(navController: NavController) {
    val searchQuery = remember { mutableStateOf("") }
    val showMenu = remember { mutableStateOf(false) }

    val exercicios = listOf(
        Exercicio(
            id = 1,
            nome = "Supino Reto",
            grupoMuscular = "Peito",
            corGrupo = Color(0xFFEF4444),
            descricao = "Exercício para desenvolvimento do peitoral maior",
            equipamento = "Barra"
        ),
        Exercicio(
            id = 2,
            nome = "Agachamento Livre",
            grupoMuscular = "Pernas",
            corGrupo = Color(0xFFF97316),
            descricao = "Exercício composto para pernas e glúteos",
            equipamento = "Barra"
        ),
        Exercicio(
            id = 3,
            nome = "Puxada Frontal",
            grupoMuscular = "Costas",
            corGrupo = Color(0xFF3B82F6),
            descricao = "Desenvolvimento do latíssimo do dorso",
            equipamento = "Máquina"
        ),
        Exercicio(
            id = 4,
            nome = "Desenvolvimento Militar",
            grupoMuscular = "Ombro",
            corGrupo = Color(0xFFA855F7),
            descricao = "Exercício para deltoides",
            equipamento = "Barra"
        ),
        Exercicio(
            id = 5,
            nome = "Rosca Direta",
            grupoMuscular = "Bíceps",
            corGrupo = Color(0xFF10B981),
            descricao = "Isolamento de Biceps braquial",
            equipamento = "Barra/Halteres"
        )
    )

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
                    "Exercícios",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = InterFont
                )

                Text(
                    "Catálogo de exercícios disponíveis",
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
                        "+ Novo Exercício",
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
                            "Buscar exercícios...",
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

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(exercicios.size) { index ->
                        ExercicioCard(exercicio = exercicios[index])
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
fun ExercicioCard(exercicio: Exercicio) {
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
                        exercicio.nome,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = InterFont
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .background(
                                color = exercicio.corGrupo,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            exercicio.grupoMuscular,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontFamily = InterFont
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Deletar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                exercicio.descricao,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = InterFont
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "Equipamento:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = InterFont
                )

                Text(
                    exercicio.equipamento,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = InterFont
                )
            }
        }
    }
}