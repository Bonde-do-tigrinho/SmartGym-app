package org.smartgym.auth

data class AuthResult(val sucesso: Boolean, val mensagem: String, val papel: String? = null)

val mockUsuarios = mutableListOf(
    MockUsuario(
        nome = "Leandro Silva",
        email = "leandro@smartgym.com",
        senha = "123456",
        telefone = "(11) 99999-0001",
        papel = "aluno"
    ),
    MockUsuario(
        nome = "Rafael Silva",
        email = "prof@smartgym.com",
        senha = "prof123",
        telefone = "(11) 99999-0002",
        papel = "professor"
    ),
    MockUsuario(
        nome = "Admin Gym",
        email = "admin@smartgym.com",
        senha = "admin123",
        telefone = "(11) 99999-0003",
        papel = "admin"
    )
)

data class MockUsuario(
    val nome: String,
    val email: String,
    val senha: String,
    val telefone: String,
    val papel: String
)

object MockAuth {

    fun getRoleByEmail(email: String): String {
        return when (email.lowercase()) {
            "admin@smartgym.com"    -> "admin"
            "prof@smartgym.com"     -> "professor"
            else                    -> "aluno"
        }
    }

    fun validarLogin(email: String, senha: String): String? {
        if (email.isBlank()) return "⚠️ Informe seu email"
        if (!email.contains("@")) return "⚠️ Email inválido"
        if (senha.isBlank()) return "⚠️ Informe sua senha"
        return null
    }

    fun validarCadastro(
        nome: String,
        email: String,
        telefone: String,
        senha: String,
        confirmarSenha: String
    ): String? {
        if (nome.isBlank()) return "⚠️ Informe seu nome"
        if (nome.length < 3) return "⚠️ Nome muito curto"
        if (email.isBlank()) return "⚠️ Informe seu email"
        if (!email.contains("@")) return "⚠️ Email inválido"
        if (telefone.filter { it.isDigit() }.length != 11)
            return "⚠️ Telefone inválido — informe DDD + número"
        if (senha.isBlank()) return "⚠️ Informe uma senha"
        if (senha.length < 6) return "⚠️ A senha deve ter no mínimo 6 caracteres"
        if (senha != confirmarSenha) return "⚠️ As senhas não coincidem"
        return null
    }

    suspend fun login(email: String, senha: String): AuthResult {
        kotlinx.coroutines.delay(1200)

        val usuario = mockUsuarios.find {
            it.email.lowercase() == email.lowercase() && it.senha == senha
        }

        return if (usuario != null) {
            // ✅ RETORNA: sucesso, mensagem e PAPEL DO USUÁRIO
            AuthResult(
                sucesso = true,
                mensagem = "Bem-vindo, ${usuario.nome}!",
                papel = usuario.papel  // 👈 IMPORTANTE: papel aqui
            )
        } else {
            AuthResult(
                sucesso = false,
                mensagem = "Email ou senha incorretos",
                papel = null
            )
        }
    }

    suspend fun cadastrar(
        nome: String,
        email: String,
        telefone: String,
        senha: String
    ): AuthResult {
        kotlinx.coroutines.delay(1500)

        val jaExiste = mockUsuarios.any { it.email.lowercase() == email.lowercase() }
        if (jaExiste) {
            return AuthResult(
                sucesso = false,
                mensagem = "Este email já está cadastrado",
                papel = null
            )
        }

        mockUsuarios.add(
            MockUsuario(
                nome = nome,
                email = email,
                senha = senha,
                telefone = telefone,
                papel = "aluno"
            )
        )
        return AuthResult(
            sucesso = true,
            mensagem = "Conta criada com sucesso!",
            papel = "aluno"
        )
    }
}