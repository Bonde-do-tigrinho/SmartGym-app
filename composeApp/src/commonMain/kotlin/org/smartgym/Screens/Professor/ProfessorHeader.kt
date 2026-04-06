package org.smartgym.Screens.Professor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
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
import smartgym.composeapp.generated.resources.Res
import smartgym.composeapp.generated.resources.inter_bold
import smartgym.composeapp.generated.resources.inter_regular
import smartgym.composeapp.generated.resources.inter_semibold

private val InterFont @Composable get() = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
    Font(Res.font.inter_bold, FontWeight.Bold)
)

data class MenuItemProf(
    val nome: String,
    val isSelected: Boolean = false,
    val onSelect: () -> Unit = {}
)

@Composable
fun ProfessorHeader(navController: NavController) {
    val showMenu = remember { mutableStateOf(false) }

    // Header apenas
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = Color.Black,
            modifier = Modifier
                .size(28.dp)
                .clickable { showMenu.value = !showMenu.value }
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            "GYM",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontFamily = InterFont
        )
    }

    Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color(0xFFEEEEEE)))

    // Menu Lateral
    if (showMenu.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { showMenu.value = false }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .width(280.dp)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Logo e cabeçalho do menu
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(16.dp)
            ) {
                Text(
                    "GYM",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = InterFont
                )
                Text(
                    "Área do Instrutor",
                    fontSize = 14.sp,
                    color = Color(0xFF888888),
                    fontFamily = InterFont
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Menu Items
            MenuItemProfessor(
                nome = "Dashboard",
                isSelected = false,
                onClick = {
                    showMenu.value = false
                    // Navegar para Dashboard
                }
            )

            MenuItemProfessor(
                nome = "Exercícios",
                isSelected = true,  // Destacado em amarelo
                onClick = {
                    showMenu.value = false
                    // Navegar para Exercícios
                }
            )

            MenuItemProfessor(
                nome = "Fichas",
                isSelected = false,
                onClick = {
                    showMenu.value = false
                    // Navegar para Fichas
                }
            )

            MenuItemProfessor(
                nome = "Avaliações",
                isSelected = false,
                onClick = {
                    showMenu.value = false
                    // Navegar para Avaliações
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFEEEEEE))
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Meu Perfil e Sair
            MenuItemProfessor(
                nome = "Meu Perfil",
                isSelected = false,
                onClick = {
                    showMenu.value = false
                    // Navegar para Perfil
                }
            )

            MenuItemProfessor(
                nome = "Sair",
                isSelected = false,
                onClick = {
                    showMenu.value = false
                    // Logout
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MenuItemProfessor(
    nome: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) Color(0xFFCDDC39) else Color.White
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            nome,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.Black else Color(0xFF333333),
            fontFamily = InterFont
        )
    }
}

