package org.smartgym.Screens.Auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.smartgym.Screen
import org.smartgym.UserRole
import org.smartgym.auth.MockAuth

@Composable
fun LoginScreen(
    navController: NavController? = null,  // ← Mantém compatibilidade
    onLoginSuccess: ((UserRole) -> Unit)? = null  // ← Novo callback
) {
    val colors = MaterialTheme.colorScheme
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }
    var carregando by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = colors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(72.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(colors.primary, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = colors.onPrimary,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "FITGYM.",
                color = colors.onBackground,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            Text(
                "Sistema de Gestão de Academia",
                color = colors.onSurfaceVariant,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(48.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Email", color = colors.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("seu@email.com", color = colors.onSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.surfaceVariant,
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                        focusedTextColor = colors.onSurface,
                        unfocusedTextColor = colors.onSurface
                    )
                )
            }

            Spacer(Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Senha", color = colors.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = senha,
                    onValueChange = { senha = it },
                    placeholder = { Text("••••••••", color = colors.onSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                            Icon(
                                if (senhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = colors.onSurfaceVariant
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.surfaceVariant,
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                        focusedTextColor = colors.onSurface,
                        unfocusedTextColor = colors.onSurface
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                TextButton(onClick = { /* TODO: navController.navigate("esqueceu_senha") */ }) {
                    Text("Esqueceu sua senha?", color = colors.onSurfaceVariant, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val erro = MockAuth.validarLogin(email, senha)
                    if (erro != null) {
                        scope.launch { snackbarHostState.showSnackbar(erro) }
                    } else {
                        carregando = true
                        scope.launch {
                            try {
                                // ✅ NOVO: MockAuth.login() agora retorna papel
                                val resultado = MockAuth.login(email, senha)
                                carregando = false

                                if (resultado.sucesso && resultado.papel != null) {
                                    // ✅ Login com sucesso!
                                    scope.launch {
                                        snackbarHostState.showSnackbar("✅ Login efetuado com sucesso!")
                                    }

                                    // ✅ NOVO: Converter papel para UserRole
                                    val userRole = when (resultado.papel) {
                                        "aluno" -> UserRole.ALUNO
                                        "professor" -> UserRole.PROFESSOR
                                        "admin" -> UserRole.ADMIN
                                        else -> return@launch
                                    }

                                    // ✅ NOVO: Se houver callback (novo padrão), use-o
                                    if (onLoginSuccess != null) {
                                        onLoginSuccess(userRole)
                                    } else if (navController != null) {
                                        // ✅ FALLBACK: Se houver navController (padrão antigo), navege
                                        val destino = when (resultado.papel) {
                                            "admin" -> Screen.HomeAdmin.route
                                            "professor" -> Screen.HomeProfessor.route
                                            else -> Screen.HomeAluno.route
                                        }
                                        navController.navigate(destino) {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                } else {
                                    // ❌ Erro no login
                                    scope.launch {
                                        snackbarHostState.showSnackbar("❌ ${resultado.mensagem}")
                                    }
                                }
                            } catch (e: Exception) {
                                carregando = false
                                scope.launch {
                                    snackbarHostState.showSnackbar("❌ Erro: ${e.message}")
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                ),
                enabled = !carregando
            ) {
                if (carregando) {
                    CircularProgressIndicator(
                        color = colors.onPrimary,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Entrar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Não tem uma conta? ", color = colors.onSurfaceVariant, fontSize = 14.sp)
                TextButton(
                    onClick = { navController?.navigate("cadastro") },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Crie agora!", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}