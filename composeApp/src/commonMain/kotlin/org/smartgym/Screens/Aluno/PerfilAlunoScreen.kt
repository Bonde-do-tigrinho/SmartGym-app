package org.smartgym.Screens.Aluno

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.smartgym.viewModel.aluno.AlunoPerfilViewModel

@Composable
fun PerfilAlunoScreen(
    navController: NavController,
    viewModel: AlunoPerfilViewModel,
    onLogout: () -> Unit
) {
    val perfil by viewModel.perfil.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val colors = MaterialTheme.colorScheme

    var showFeatureNotImplementedDialog by remember { mutableStateOf(false) }

    val nome = perfil?.nome ?: "Usuário"
    val email = perfil?.email ?: ""
    val initials = nome.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase()

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = colors.primary)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = colors.onPrimary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(nome.uppercase(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
                Text(email, fontSize = 13.sp, color = colors.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("Configurações da Conta", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.primary)
        Spacer(modifier = Modifier.height(16.dp))

        PerfilOpcao(Icons.Default.Person, "Meus Dados", "Edite suas informações pessoais") {
            showFeatureNotImplementedDialog = true
        }
        PerfilOpcao(Icons.Default.Lock, "Privacidade e Senha", "Altere sua senha de acesso") {
            showFeatureNotImplementedDialog = true
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onLogout() },
            colors = ButtonDefaults.buttonColors(containerColor = colors.errorContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sair", tint = colors.onErrorContainer)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sair do Aplicativo", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.onErrorContainer)
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
    if (showFeatureNotImplementedDialog) {
        AlertDialog(
            onDismissRequest = { showFeatureNotImplementedDialog = false },
            title = {
                Text("Em Desenvolvimento", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Essa funcionalidade ainda não foi implementada no sistema SmartGym.")
            },
            confirmButton = {
                TextButton(
                    onClick = { showFeatureNotImplementedDialog = false }
                ) {
                    Text("Entendido", color = colors.primary, fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = colors.surface
        )
    }
}

@Composable
fun PerfilOpcao(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = "Ir", tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}