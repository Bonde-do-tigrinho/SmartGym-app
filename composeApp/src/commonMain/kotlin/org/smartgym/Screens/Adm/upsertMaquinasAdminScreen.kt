package org.smartgym.Screens.Adm

import MaquinaViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import org.smartgym.model.Adm.Maquina

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioMaquinaScreen(
    viewModel: MaquinaViewModel,
    maquinaInicial: Maquina?,
    modifier: Modifier = Modifier,
    onVoltar: () -> Unit
) {
    var nome by remember { mutableStateOf(maquinaInicial?.nome ?: "") }
    var localizacao by remember { mutableStateOf(maquinaInicial?.localizacao ?: "") }

    // Usa diretamente o valor da API
    var status by remember { mutableStateOf(maquinaInicial?.status?.uppercase() ?: "LIVRE") }

    val isLoading by viewModel.isLoading.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    // Lista exata de como a API espera
    val statusOptions = listOf("LIVRE", "OCUPADA", "MANUTENCAO")
    val isEditando = maquinaInicial != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVoltar) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = "Máquinas", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(
                    text = if (isEditando) "Editando máquina" else "Adicionando nova máquina",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Nome", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            placeholder = { Text("Ex: Supino inclinado") },
            leadingIcon = { Icon(Icons.Rounded.FitnessCenter, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Localização", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = localizacao,
            onValueChange = { localizacao = it },
            placeholder = { Text("Ex: Centro") },
            leadingIcon = { Icon(Icons.Outlined.Assignment, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Status", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
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

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val maquinaPronta = Maquina(
                    id = maquinaInicial?.id,
                    nome = nome,
                    localizacao = localizacao,
                    status = status // Já está usando o texto exato da API
                )

                if (isEditando && maquinaInicial?.id != null) {
                    viewModel.atualizarMaquina(maquinaInicial.id.toLong(), maquinaPronta)
                } else {
                    viewModel.criarMaquina(maquinaPronta)
                }
                onVoltar()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
            } else {
                Text(if (isEditando) "Atualizar" else "Salvar", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}