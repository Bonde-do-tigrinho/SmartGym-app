package org.smartgym.Screens.Auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.smartgym.Screens.Adm.CampoTexto
import org.smartgym.viewModel.AuthState
import org.smartgym.viewModel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val colors = MaterialTheme.colorScheme
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var enviado by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        when (val s = state) {
            is AuthState.Error -> {
                snackbarHostState.showSnackbar(s.message)
                viewModel.resetState()
            }
            is AuthState.Success -> {
                enviado = true
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
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = androidx.compose.ui.Modifier
                        .wrapContentSize()
                        .then(
                            androidx.compose.ui.Modifier
                        )
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = colors.onBackground)
                }
            }

            Spacer(Modifier.height(48.dp))

            if (enviado) {
                Icon(
                    Icons.Default.MarkEmailRead,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(Modifier.height(24.dp))
                Text("Email enviado!", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = colors.onBackground)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Se este email estiver cadastrado, você receberá as instruções para redefinir sua senha. Verifique também a caixa de spam.",
                    color = colors.onSurfaceVariant,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Voltar para o Login", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            } else {
                Text(
                    "RECUPERAR\nSENHA.",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.onBackground,
                    letterSpacing = 1.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Informe seu email e enviaremos as instruções para redefinir sua senha.",
                    color = colors.onSurfaceVariant,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(32.dp))

                CampoTexto(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "seu@email.com",
                    colors = colors,
                    keyboardType = KeyboardType.Email
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.recuperarSenha(email) { enviado = true } },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    enabled = state !is AuthState.Loading
                ) {
                    if (state is AuthState.Loading) {
                        CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Enviar instruções", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}