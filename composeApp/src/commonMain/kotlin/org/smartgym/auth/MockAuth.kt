package org.smartgym.auth

data class AuthResult(val sucesso: Boolean, val mensagem: String)

val mockUsuarios = mutableListOf(
    MockUsuario("Leandro Silva", "leandro@smartgym.com", "123456", "(11) 99999-0001"),
    MockUsuario("Admin Gym", "admin@smartgym.com", "admin123", "(11) 99999-0002")
)

data class MockUsuario(
    val nome: String,
    val email: String,
    val senha: String,
    val telefone: String
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
            AuthResult(sucesso = true, mensagem = "Bem-vindo, ${usuario.nome}!")
        } else {
            AuthResult(sucesso = false, mensagem = "Email ou senha incorretos")
        }
    }

    suspend fun cadastrar(
        nome: String,
        email: String,
        telefone: String,
        senha: String
    ): AuthResult {
        kotlinx.coroutines.delay(1500) // simula latência de rede

        val jaExiste = mockUsuarios.any { it.email.lowercase() == email.lowercase() }
        if (jaExiste) {
            return AuthResult(sucesso = false, mensagem = "Este email já está cadastrado")
        }

        mockUsuarios.add(MockUsuario(nome, email, senha, telefone))
        return AuthResult(sucesso = true, mensagem = "Conta criada com sucesso!")
    }
}