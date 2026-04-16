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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation.NavController
import org.jetbrains.compose.resources.Font
import org.smartgym.model.professor.Exercicio
import org.smartgym.model.professor.TipoExercicio
import org.smartgym.theme.SmartGymGreen
import org.smartgym.viewModel.Professor.ExerciciosViewModel
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
fun ExerciciosScreen(navController: NavController, viewModel: ExerciciosViewModel) {
    val searchQuery = remember { mutableStateOf("") }
    val showMenu = remember { mutableStateOf(false) }
    val showForm = remember { mutableStateOf(false) }

    val exercicios by viewModel.exercicios.collectAsState()
    val nome by viewModel.nome.collectAsState()
    val descricao by viewModel.descricao.collectAsState()
    val tipo by viewModel.tipo.collectAsState()
    val maquinaId by viewModel.maquinaId.collectAsState()
    val editingId by viewModel.editingId.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAll()
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

            // AQUI COMEÇA A MUDANÇA: Usamos apenas LazyColumn para gerenciar o scroll da tela inteira
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // item 1: Agrupa todo o cabeçalho, botão de novo exercício, formulário e campo de busca
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
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
                            onClick = { showForm.value = !showForm.value },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SmartGymGreen
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                if (editingId != null) "Editar Exercício" else "+ Novo Exercício",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black,
                                fontFamily = InterFont
                            )
                        }

                        if (showForm.value) {
                            Spacer(modifier = Modifier.height(16.dp))
                            ExercicioForm(
                                nome = nome,
                                onNomeChange = viewModel::updateNome,
                                descricao = descricao,
                                onDescricaoChange = viewModel::updateDescricao,
                                tipo = tipo,
                                onTipoChange = viewModel::updateTipo,
                                maquinaId = maquinaId,
                                onMaquinaIdChange = viewModel::updateMaquinaId,
                                onSave = {
                                    viewModel.save()
                                    showForm.value = false
                                },
                                onCancel = {
                                    viewModel.clearForm()
                                    showForm.value = false
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = searchQuery.value,
                            onValueChange = {
                                searchQuery.value = it
                                if (it.isBlank()) viewModel.loadAll() else viewModel.loadByNome(it)
                            },
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
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                // item 2: A lista de exercícios em si (gerada dinamicamente)
                items(exercicios.size) { index ->
                    ExercicioCard(
                        exercicio = exercicios[index],
                        onEdit = { viewModel.loadById(it.id!!) ; showForm.value = true },
                        onDelete = { viewModel.delete(it.id!!) }
                    )
                }

                // item 3: Espaçador final
                item {
                    Spacer(modifier = Modifier.height(40.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercicioForm(
    nome: String,
    onNomeChange: (String) -> Unit,
    descricao: String,
    onDescricaoChange: (String) -> Unit,
    tipo: TipoExercicio,
    onTipoChange: (TipoExercicio) -> Unit,
    maquinaId: Long?,
    onMaquinaIdChange: (Long?) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text("Nome", fontFamily = InterFont, fontWeight = FontWeight.SemiBold)
        TextField(
            value = nome,
            onValueChange = onNomeChange,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Descrição", fontFamily = InterFont, fontWeight = FontWeight.SemiBold)
        TextField(
            value = descricao,
            onValueChange = onDescricaoChange,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            TextField(
                value = tipo.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                TipoExercicio.values().forEach { tipoOption ->
                    DropdownMenuItem(
                        text = { Text(tipoOption.name) },
                        onClick = {
                            onTipoChange(tipoOption)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (tipo == TipoExercicio.MAQUINA) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("ID da Máquina", fontFamily = InterFont, fontWeight = FontWeight.SemiBold)
            TextField(
                value = maquinaId?.toString() ?: "",
                onValueChange = { onMaquinaIdChange(it.toLongOrNull()) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = onSave, modifier = Modifier.weight(1f)) {
                Text("Salvar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onCancel, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                Text("Cancelar")
            }
        }
    }
}

@Composable
fun ExercicioCard(exercicio: Exercicio, onEdit: (Exercicio) -> Unit, onDelete: (Exercicio) -> Unit) {
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
                                color = if (exercicio.tipo == TipoExercicio.LIVRE) Color.Green else Color.Blue,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            exercicio.tipo.name,
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
                        onClick = { onEdit(exercicio) },
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
                        onClick = { onDelete(exercicio) },
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

            if (exercicio.tipo == TipoExercicio.MAQUINA && exercicio.maquinaId != null) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "Máquina ID:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = InterFont
                    )

                    Text(
                        exercicio.maquinaId.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = InterFont
                    )
                }
            }
        }
    }
}