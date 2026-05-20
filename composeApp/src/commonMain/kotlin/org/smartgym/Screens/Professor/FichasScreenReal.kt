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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import org.jetbrains.compose.resources.Font
import org.smartgym.Screen
import org.smartgym.model.professor.FichaTreino
import org.smartgym.theme.SmartGymGreen
import org.smartgym.util.formatDateToUi
import org.smartgym.viewModel.Professor.CriarFichaViewModel
import org.smartgym.viewModel.Professor.FichasViewModel
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
fun FichasScreenReal(navController: NavController, viewModel: FichasViewModel, criarFichaViewModel: CriarFichaViewModel) {
    val fichas by viewModel.fichas.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var fichaToDelete by remember { mutableStateOf<FichaTreino?>(null) }
    LaunchedEffect(Unit) {
        viewModel.loadAll()
        viewModel.loadAlunos()
    }
    val fichasFiltradas = fichas.filter { ficha ->
        val alunoNome = viewModel.alunoNome(ficha.alunoId)
        val query = searchQuery.trim()
        ficha.focoTreino.contains(query, ignoreCase = true) ||
            alunoNome.contains(query, ignoreCase = true) ||
            ficha.alunoId.toString() == query ||
            (ficha.id?.toString() == query)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                    text = "Fichas de Treino",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = InterFont
                )
                Text(
                    text = "Gerencie as fichas dos alunos",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = InterFont
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        criarFichaViewModel.clearForm()
                        navController.navigate(Screen.NovaFicha.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SmartGymGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "+ Nova Ficha",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        fontFamily = InterFont
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    placeholder = {
                        Text(
                            text = "Buscar fichas...",
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(fichasFiltradas, key = { it.id ?: -1L }) { ficha ->
                        FichaItemReal(
                            ficha = ficha,
                            alunoNome = viewModel.alunoNome(ficha.alunoId),
                            onEdit = {
                                ficha.id?.let(criarFichaViewModel::prepareForEdit)
                                navController.navigate(Screen.EditarFicha.route)
                            },
                            onDelete = { fichaToDelete = ficha }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
        if (fichaToDelete != null) {
            DeleteFichaDialogReal(
                fichaNome = fichaToDelete!!.focoTreino,
                onConfirm = {
                    fichaToDelete?.id?.let(viewModel::delete)
                    fichaToDelete = null
                },
                onDismiss = { fichaToDelete = null }
            )
        }
    }
}
@Composable
fun FichaItemReal(
    ficha: FichaTreino,
    alunoNome: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp), clip = false)
            .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = alunoNome,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = InterFont
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .background(color = SmartGymGreen, shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = ficha.focoTreino,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    fontFamily = InterFont
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                InfoRowReal(label = "Criado em:", value = formatDateToUi(ficha.dataCriacao).ifBlank { "-" })
                InfoRowReal(label = "Valido ate:", value = formatDateToUi(ficha.vigencia).ifBlank { "-" })
                InfoRowReal(label = "Exercicios:", value = ficha.exercicios.size.toString())
            }
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Editar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = InterFont
                    )
                }
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Excluir",
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
fun InfoRowReal(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = InterFont
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = InterFont
        )
    }
}
@Composable
fun DeleteFichaDialogReal(
    fichaNome: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Excluir ficha",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFont,
                        color = Color.Black
                    )
                    IconButton(onClick = onDismiss) {
                        Text("✕")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Quer excluir a ficha $fichaNome?",
                    fontSize = 16.sp,
                    fontFamily = InterFont,
                    color = Color(0xFF8E8E8E)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFBDBD),
                            contentColor = Color(0xFFD32F2F)
                        )
                    ) {
                        Text("Deletar", fontFamily = InterFont)
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF5F5F5),
                            contentColor = Color(0xFF8E8E8E)
                        )
                    ) {
                        Text("Cancelar", fontFamily = InterFont)
                    }
                }
            }
        }
    }
}
