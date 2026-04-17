package org.smartgym.Screens.Auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.smartgym.Screen
import org.smartgym.UserRole
import org.smartgym.auth.MockAuth

@Composable
fun LoginScreen(
    navController: NavController? = null,
    onLoginSuccess: ((UserRole) -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current // Controla o teclado

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }
    var carregando by remember { mutableStateOf(false) }

    // --- LÓGICA DE LOGIN CENTRALIZADA ---
    val performLogin = {
        val erro = MockAuth.validarLogin(email, senha)
        if (erro != null) {
            scope.launch { snackbarHostState.showSnackbar(erro) }
        } else {
            carregando = true
            scope.launch {
                try {
                    val resultado = MockAuth.login(email, senha)
                    carregando = false

                    if (resultado.sucesso && resultado.papel != null) {
                        scope.launch { snackbarHostState.showSnackbar("✅ Login efetuado com sucesso!") }
                        val userRole = when (resultado.papel) {
                            "aluno" -> UserRole.ALUNO
                            "professor" -> UserRole.PROFESSOR
                            "admin" -> UserRole.ADMIN
                            else -> return@launch
                        }

                        if (onLoginSuccess != null) {
                            onLoginSuccess(userRole)
                        }
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("❌ ${resultado.mensagem}") }
                    }
                } catch (e: Exception) {
                    carregando = false
                    scope.launch { snackbarHostState.showSnackbar("❌ Erro: ${e.message}") }
                }
            }
        }
    }

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

            Text("FITGYM.", color = colors.onBackground, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
            Text("Sistema de Gestão de Academia", color = colors.onSurfaceVariant, fontSize = 14.sp)

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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary, unfocusedBorderColor = colors.surfaceVariant,
                        focusedContainerColor = colors.surface, unfocusedContainerColor = colors.surface,
                        focusedTextColor = colors.onSurface, unfocusedTextColor = colors.onSurface
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

                    // --- MÁGICA DO TECLADO AQUI ---
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        performLogin() // Chama o login
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onKeyEvent { event ->
                            if (event.key == Key.Enter && event.type == KeyEventType.KeyUp) {
                                focusManager.clearFocus()
                                performLogin() // Chama o login no PC
                                true
                            } else false
                        },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                            Icon(if (senhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, tint = colors.onSurfaceVariant)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary, unfocusedBorderColor = colors.surfaceVariant,
                        focusedContainerColor = colors.surface, unfocusedContainerColor = colors.surface,
                        focusedTextColor = colors.onSurface, unfocusedTextColor = colors.onSurface
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                TextButton(onClick = { /* TODO */ }) {
                    Text("Esqueceu sua senha?", color = colors.onSurfaceVariant, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { performLogin() }, // Chama o login no botão
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.onPrimary),
                enabled = !carregando
            ) {
                if (carregando) {
                    CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Entrar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("Não tem uma conta? ", color = colors.onSurfaceVariant, fontSize = 14.sp)
                TextButton(onClick = { navController?.navigate("cadastro") }, contentPadding = PaddingValues(0.dp)) {
                    Text("Crie agora!", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}