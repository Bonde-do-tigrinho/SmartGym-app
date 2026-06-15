package org.smartgym.Screens.Adm

import org.smartgym.viewModel.Adm.MaquinaIotViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smartgym.model.Adm.MaquinaIot
import org.smartgym.model.Adm.StatusMaquinaIot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioMaquinaIotScreen(
    viewModel: MaquinaIotViewModel,
    maquinaIotInicial: MaquinaIot?,
    modifier: Modifier = Modifier,
    onVoltar: () -> Unit
) {
    var nome by remember { mutableStateOf(maquinaIotInicial?.nome ?: "") }
    var localizacao by remember { mutableStateOf(maquinaIotInicial?.localizacao ?: "") }
    var deviceId by remember { mutableStateOf(maquinaIotInicial?.deviceId ?: "") }
    var status by remember { mutableStateOf(maquinaIotInicial?.status?.name?.uppercase() ?: "LIVRE") }
    val isLoading by viewModel.isLoading.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    var categoria by remember { mutableStateOf(maquinaIotInicial?.categoria ?: "Cardio") }
    var dropdownExpandido by remember { mutableStateOf(false) }

    val categoriasDisponiveis = listOf("Cardio", "Peito", "Costas", "Pernas")

    val statusOptions = listOf("LIVRE", "OCUPADA", "MANUTENCAO")
    val isEditando = maquinaIotInicial != null


    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        item{
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVoltar) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Máquinas IOTs",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isEditando) "Editando máquina IOT" else "Adicionando nova máquina IOT",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        item{
            Text(
                "ID do Dispositivo (MQTT)",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = deviceId,
                onValueChange = { deviceId = it },
                placeholder = { Text("Ex: esteira-01") },
                leadingIcon = { Icon(Icons.Rounded.FitnessCenter, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isEditando, // Bloqueia o ID na edição para não quebrar o histórico do MQTT
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        item{
            Text("Nome", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                placeholder = { Text("Ex: Esteira IOT") },
                leadingIcon = { Icon(Icons.Rounded.FitnessCenter, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        item{
            Text(
                "Localização",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = localizacao,
                onValueChange = { localizacao = it },
                placeholder = { Text("Ex: Área de cardio") },
                leadingIcon = { Icon(Icons.Outlined.Assignment, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        item{
            Text("Categoria para Filtro:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = dropdownExpandido,
                onExpandedChange = { dropdownExpandido = !dropdownExpandido }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true, // Impede o usuário de digitar algo fora da lista
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpandido) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = dropdownExpandido,
                    onDismissRequest = { dropdownExpandido = false }
                ) {
                    categoriasDisponiveis.forEach { opcao ->
                        DropdownMenuItem(
                            text = { Text(opcao) },
                            onClick = {
                                categoria = opcao
                                dropdownExpandido = false
                            }
                        )
                    }
                }
            }
        }

        item{
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Status",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = status,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    statusOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                status = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        item{
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val maquinaIotPronta = MaquinaIot(
                        id = maquinaIotInicial?.id,
                        deviceId = deviceId.trim(),
                        nome = nome.trim(),
                        localizacao = localizacao.trim(),
                        categoria = categoria,
                        status = StatusMaquinaIot.valueOf(status),
                    )

                    if (isEditando && maquinaIotInicial?.id != null) {
                        viewModel.atualizarMaquinaIot(maquinaIotInicial.id, maquinaIotPronta)
                    } else {
                        println("📡 [BUTTON CLICK] Disparando criarMaquinaIot para a API...")
                        viewModel.criarMaquinaIot(maquinaIotPronta)
                    }
                    onVoltar()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),

                enabled = !isLoading && nome.isNotBlank() && deviceId.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                } else {
                    Text(
                        if (isEditando) "Atualizar" else "Salvar",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}