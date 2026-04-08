package org.smartgym.Screens.Professor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jetbrains.compose.resources.Font
import org.smartgym.Screen
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
fun ProfessorMenuOverlay(
    showMenu: Boolean,
    onDismiss: () -> Unit,
    navController: NavController
) {
    if (showMenu) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() }
        )


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
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

                Spacer(modifier = Modifier.height(24.dp).fillMaxWidth())

                val navBackStackEntry = navController.currentBackStackEntryAsState().value
                val currentRoute = navBackStackEntry?.destination?.route

                MenuItemProfessor(
                    nome = "Dashboard",
                    isSelected = currentRoute == Screen.HomeProfessor.route,
                    onClick = {
                        onDismiss()
                        navController.navigate(Screen.HomeProfessor.route) {
                            popUpTo(Screen.HomeProfessor.route) {
                                inclusive = false
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                MenuItemProfessor(
                    nome = "Exercícios",
                    isSelected = currentRoute == Screen.Exercicios.route,
                    onClick = {
                        onDismiss()
                        navController.navigate(Screen.Exercicios.route) {
                            popUpTo(Screen.HomeProfessor.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                MenuItemProfessor(
                    nome = "Fichas",
                    isSelected = currentRoute == Screen.Fichas.route,
                    onClick = {
                        onDismiss()
                        navController.navigate(Screen.Fichas.route) {
                            popUpTo(Screen.HomeProfessor.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                MenuItemProfessor(
                    nome = "Avaliações",
                    isSelected = currentRoute == Screen.Avaliacoes.route,
                    onClick = {
                        onDismiss()
                        navController.navigate(Screen.Avaliacoes.route) {
                            popUpTo(Screen.HomeProfessor.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp).fillMaxWidth())
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFEEEEEE))
                )
                Spacer(modifier = Modifier.height(24.dp).fillMaxWidth())

                MenuItemProfessor(
                    nome = "Meu Perfil",
                    isSelected = false,
                    onClick = {
                        onDismiss()
                    }
                )

                MenuItemProfessor(
                    nome = "Sair",
                    isSelected = false,
                    onClick = {
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(24.dp).fillMaxWidth())
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clickable { onDismiss() }
            )
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
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .background(
                color = if (isSelected) Color(0xFFCDDC39) else Color.Transparent,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 8.dp)
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