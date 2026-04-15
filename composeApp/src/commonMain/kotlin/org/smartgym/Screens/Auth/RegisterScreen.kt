package org.smartgym.Screens.Auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.smartgym.auth.MockAuth

class TelefoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.take(11)
        val formatted = buildString {
            digits.forEachIndexed { i, c ->
                when (i) {
                    0 -> append("($c")
                    1 -> append("$c) ")
                    6 -> append("$c-")
                    else -> append(c)
                }
            }
        }
        val offsetMap = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 0 -> 0
                    offset <= 1 -> offset + 1
                    offset <= 2 -> offset + 2
                    offset <= 6 -> offset + 3
                    offset <= 11 -> offset + 4
                    else -> formatted.length
                }
            }
            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 1 -> 0
                    offset <= 3 -> offset - 1
                    offset <= 5 -> offset - 2
                    offset <= 9 -> offset - 3
                    else -> offset - 4
                }.coerceIn(0, digits.length)
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMap)
    }
}

@Composable
fun RegisterScreen(navController: NavController) {
    val colors = MaterialTheme.colorScheme
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }
    var confirmarSenhaVisivel by remember { mutableStateOf(false) }
    var carregando by remember { mutableStateOf(false) }

    // --- LÓGICA DE CADASTRO CENTRALIZADA ---
    val performRegister = {
        val erro = MockAuth.validarCadastro(nome, email, telefone, senha, confirmarSenha)
        if (erro != null) {
            scope.launch { snackbarHostState.showSnackbar(erro) }
        } else {
            carregando = true
            scope.launch {
                val resultado = MockAuth.cadastrar(nome, email, telefone, senha)
                carregando = false
                if (resultado.sucesso) {
                    snackbarHostState.showSnackbar("✅ Conta criada com sucesso! Faça login.")
                    navController.navigate("login") {
                        popUpTo("cadastro") { inclusive = true }
                    }
                } else {
                    snackbarHostState.showSnackbar("❌ ${resultado.mensagem}")
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
                .padding(horizontal = 28.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.background(colors.surfaceVariant, RoundedCornerShape(12.dp))) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = colors.onSurface)
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("CRIAR CONTA.", color = colors.onBackground, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            Text("Preencha seus dados para começar", color = colors.onSurfaceVariant, fontSize = 14.sp)

            Spacer(Modifier.height(32.dp))

            CampoTexto("Nome completo", nome, { nome = it }, "João da Silva", colors, imeAction = ImeAction.Next)
            Spacer(Modifier.height(16.dp))
            CampoTexto("Email", email, { email = it }, "seu@email.com", colors, KeyboardType.Email, imeAction = ImeAction.Next)
            Spacer(Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Telefone", color = colors.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = telefone,
                    onValueChange = { input -> telefone = input.filter { it.isDigit() }.take(11) },
                    placeholder = { Text("(11) 99999-9999", color = colors.onSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    visualTransformation = TelefoneVisualTransformation(),
                    supportingText = {
                        Text("${telefone.length}/11 dígitos", color = if (telefone.length == 11) colors.primary else colors.onSurfaceVariant, fontSize = 11.sp)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (telefone.length in listOf(0, 11)) colors.primary else colors.error,
                        unfocusedBorderColor = if (telefone.length in listOf(0, 11)) colors.surfaceVariant else colors.error,
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
                    placeholder = { Text("Mínimo 6 caracteres", color = colors.onSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
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

            Spacer(Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Confirmar senha", color = colors.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = confirmarSenha,
                    onValueChange = { confirmarSenha = it },
                    placeholder = { Text("Repita a senha", color = colors.onSurfaceVariant) },

                    // --- MÁGICA DO TECLADO AQUI ---
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        performRegister()
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onKeyEvent { event ->
                            if (event.key == Key.Enter && event.type == KeyEventType.KeyUp) {
                                focusManager.clearFocus()
                                performRegister()
                                true
                            } else false
                        },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    visualTransformation = if (confirmarSenhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmarSenhaVisivel = !confirmarSenhaVisivel }) {
                            Icon(if (confirmarSenhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, tint = colors.onSurfaceVariant)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (confirmarSenha.isNotEmpty() && confirmarSenha != senha) colors.error else colors.primary,
                        unfocusedBorderColor = if (confirmarSenha.isNotEmpty() && confirmarSenha != senha) colors.error else colors.surfaceVariant,
                        focusedContainerColor = colors.surface, unfocusedContainerColor = colors.surface,
                        focusedTextColor = colors.onSurface, unfocusedTextColor = colors.onSurface
                    )
                )
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { performRegister() }, // Chama o cadastro no botão
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.onPrimary),
                enabled = !carregando
            ) {
                if (carregando) {
                    CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Criar conta", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Já tem uma conta? ", color = colors.onSurfaceVariant, fontSize = 14.sp)
                TextButton(onClick = { navController.popBackStack() }, contentPadding = PaddingValues(0.dp)) {
                    Text("Entrar", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CampoTexto(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    colors: ColorScheme,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = colors.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = colors.onSurfaceVariant) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary, unfocusedBorderColor = colors.surfaceVariant,
                focusedContainerColor = colors.surface, unfocusedContainerColor = colors.surface,
                focusedTextColor = colors.onSurface, unfocusedTextColor = colors.onSurface
            )
        )
    }
}