package org.smartgym.Screens.Auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.smartgym.viewModel.AuthState
import org.smartgym.viewModel.AuthViewModel

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    token: String
) {
    val colors = MaterialTheme.colorScheme
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var novaSenha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var novaSenhaVisivel by remember { mutableStateOf(false) }
    var confirmarSenhaVisivel by remember { mutableStateOf(false) }
    var sucesso by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        when (val s = state) {
            is AuthState.Error -> {
                snackbarHostState.showSnackbar(s.message)
                viewModel.resetState()
            }
            is AuthState.Success -> {
                sucesso = true
                viewModel.resetState()
            }
            else -> {}
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
            Spacer(Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { navController.navigate("login") { popUpTo(0) } }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = colors.onBackground)
                }
            }

            Spacer(Modifier.height(48.dp))

            if (sucesso) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colors.primary, modifier = Modifier.size(80.dp))
                Spacer(Modifier.height(24.dp))
                Text("Senha redefinida!", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = colors.onBackground)
                Spacer(Modifier.height(12.dp))
                Text("Sua senha foi alterada com sucesso. Faça login com sua nova senha.", color = colors.onSurfaceVariant, fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = { navController.navigate("login") { popUpTo(0) } },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Ir para o Login", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            } else {
                Text("NOVA SENHA.", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = colors.onBackground, letterSpacing = 1.sp, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Text("Digite sua nova senha abaixo.", color = colors.onSurfaceVariant, fontSize = 14.sp)
                Spacer(Modifier.height(32.dp))

                // Campo nova senha
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Nova senha", color = colors.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = novaSenha,
                        onValueChange = { novaSenha = it },
                        placeholder = { Text("Mínimo 6 caracteres", color = colors.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        visualTransformation = if (novaSenhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { novaSenhaVisivel = !novaSenhaVisivel }) {
                                Icon(if (novaSenhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, tint = colors.onSurfaceVariant)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary, unfocusedBorderColor = colors.surfaceVariant,
                            focusedContainerColor = colors.surface, unfocusedContainerColor = colors.surface,
                            focusedTextColor = colors.onSurface, unfocusedTextColor = colors.onSurface
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Campo confirmar senha
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Confirmar senha", color = colors.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = confirmarSenha,
                        onValueChange = { confirmarSenha = it },
                        placeholder = { Text("Repita a nova senha", color = colors.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        visualTransformation = if (confirmarSenhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmarSenhaVisivel = !confirmarSenhaVisivel }) {
                                Icon(if (confirmarSenhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, tint = colors.onSurfaceVariant)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (confirmarSenha.isNotEmpty() && confirmarSenha != novaSenha) colors.error else colors.primary,
                            unfocusedBorderColor = if (confirmarSenha.isNotEmpty() && confirmarSenha != novaSenha) colors.error else colors.surfaceVariant,
                            focusedContainerColor = colors.surface, unfocusedContainerColor = colors.surface,
                            focusedTextColor = colors.onSurface, unfocusedTextColor = colors.onSurface
                        )
                    )
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.resetarSenha(token, novaSenha, confirmarSenha) {
                            sucesso = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    enabled = state !is AuthState.Loading
                ) {
                    if (state is AuthState.Loading) {
                        CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Redefinir senha", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}