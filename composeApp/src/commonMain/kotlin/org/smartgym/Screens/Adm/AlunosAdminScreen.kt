package org.smartgym.Screens.Adm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class Aluno(
    val id: Int,
    val nome: String,
    val email: String,
    val telefone: String,
    val plano: String,
    val planoCor: Color
)

@Composable
fun AlunosAdminScreen(navController: NavController, modifier: Modifier = Modifier) {
    // Dados de exemplo baseados no seu print
    val listaAlunos = listOf(
        Aluno(1, "Lucas Mendes", "lucas@email.com", "(11) 98765-4321", "Premium", Color(0xFFD9FF00)),
        Aluno(2, "Fernanda Lima", "fernanda@email.com", "(11) 98765-1234", "Basic", Color(0xFFD9FF00)),
        Aluno(3, "João Silva", "joao@email.com", "(11) 98765-5678", "Black", Color(0xFFD9FF00)),
        Aluno(4, "Lucas Mendes", "lucas@email.com", "(11) 98765-4321", "Premium", Color(0xFFD9FF00)),
        Aluno(5, "Fernanda Lima", "fernanda@email.com", "(11) 98765-1234", "Basic", Color(0xFFD9FF00)),
        Aluno(6, "João Silva", "joao@email.com", "(11) 98765-5678", "Black", Color(0xFFD9FF00)),
        Aluno(7, "Lucas Mendes", "lucas@email.com", "(11) 98765-4321", "Premium", Color(0xFFD9FF00)),
        Aluno(8, "Fernanda Lima", "fernanda@email.com", "(11) 98765-1234", "Basic", Color(0xFFD9FF00)),
        Aluno(9, "João Silva", "joao@email.com", "(11) 98765-5678", "Black", Color(0xFFD9FF00))
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. Título e Subtítulo
        Text(
            text = "Alunos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Gerencie os alunos da academia",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Botão Novo Aluno (Verde limão)
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9FF00)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
            Spacer(Modifier.width(8.dp))
            Text("Novo Aluno", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Campo de Busca
        TextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar alunos...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4. "Tabela" de Alunos
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column {
                // Cabeçalho da Tabela
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Nome", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                    Text("Contato", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                    Text("Plano", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                // Lista de Alunos
                LazyColumn {
                    items(listaAlunos) { aluno ->
                        AlunoRow(aluno)
                        Divider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AlunoRow(aluno: Aluno) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Nome
        Text(
            text = aluno.nome,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1.2f)
        )

        // Contato (Email e Telefone com ícones pequenos)
        Column(modifier = Modifier.weight(1.5f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text(aluno.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text(aluno.telefone, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Badge do Plano
        Box(
            modifier = Modifier
                .weight(0.8f)
                .clip(CircleShape)
                .background(aluno.planoCor)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = aluno.plano,
                fontSize = 11.sp,
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}