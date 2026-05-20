# 🔐 Implementação de Autenticação JWT - SmartGym App

## Resumo das Alterações

A autenticação JWT foi implementada seguindo o padrão do seu backend Spring Security. O cliente agora:

1. ✅ **Armazena o token JWT** após login bem-sucedido
2. ✅ **Envia o header `Authorization: Bearer <token>`** automaticamente em todas as requisições HTTP
3. ✅ **Limpa o token** ao fazer logout

---

## 📁 Arquivos Criados/Modificados

### 1️⃣ **TokenManager.kt** (NOVO)
**Arquivo:** `org/smartgym/auth/TokenManager.kt`

Gerencia o armazenamento e recuperação do token JWT em memória:
```kotlin
TokenManager.setToken(token)           // Salva o token
TokenManager.getToken()                // Recupera o token
TokenManager.getAuthorizationHeader()  // Retorna "Bearer <token>"
TokenManager.clearToken()              // Limpa ao fazer logout
TokenManager.hasValidToken()           // Verifica se tem token válido
```

**Nota:** Em produção (Android), considere usar `EncryptedSharedPreferences` para armazenar o token de forma segura.

---

### 2️⃣ **ApiClient.kt** (MODIFICADO)
**Arquivo:** `org/smartgym/network/ApiClient.kt`

Adicionado um **plugin de interceptador personalizado** (`AuthorizationHeaderPlugin`) que:
- Intercepta todas as requisições HTTP antes de enviar
- Adiciona automaticamente o header `Authorization: Bearer <token>` se um token estiver disponível
- Funciona de forma dinâmica (pega o token atual do `TokenManager` a cada requisição)

```kotlin
class AuthorizationHeaderPlugin : HttpClientPlugin<...> {
    // Intercepta requisições e adiciona Authorization header
}

val client = HttpClient {
    install(AuthorizationHeaderPlugin)  // Ativa o interceptador
}
```

---

### 3️⃣ **ApiAuthRepository.kt** (MODIFICADO)
**Arquivo:** `org/smartgym/repository/ApiAuthRepository.kt`

Modificado para **salvar o token JWT** quando o login é bem-sucedido:
```kotlin
// Salva o token se disponível na resposta
loginResponse?.token?.let { TokenManager.setToken(it) }
```

---

### 4️⃣ **App.kt** (MODIFICADO)
**Arquivo:** `org/smartgym/App.kt`

Adicionado a **limpeza do token** no callback `onLogout`:
```kotlin
onLogout = {
    TokenManager.clearToken()  // 🔐 Limpa o token JWT ao fazer logout
    usuarioLogado.value = null
}
```

---

## 🔄 Fluxo de Autenticação

```
1. LOGIN (LoginScreen)
   ↓
2. ApiAuthRepository.login() extrai o token da resposta
   ↓
3. TokenManager.setToken(token) armazena o token
   ↓
4. Todas as requisições HTTP incluem automaticamente
   "Authorization: Bearer <token>" (via AuthorizationHeaderPlugin)
   ↓
5. LOGOUT (Clique em "Sair")
   ↓
6. App.kt chama TokenManager.clearToken()
   ↓
7. Redirecionado para LoginScreen
```

---

## 🧪 Como Testar

1. **Login com credenciais válidas** → Token será armazenado
2. **Fazer requisições** → Verifique no backend se o header `Authorization` chegou
3. **Logout** → Token será limpo
4. **Tentar fazer requisição sem token** → Backend deve retornar 401/403

---

## 📋 Detalhes Técnicos

### Header Authorization
Formato padrão JWT: `Authorization: Bearer eyJhbGciOiJIUzI1NiIs...`

### Interceptador Ktor
O plugin `AuthorizationHeaderPlugin` funciona com:
- `HttpRequestPipeline.Before` → Intercepta antes de enviar a requisição
- Dinâmico → Pega o token atual a cada requisição
- Compatível com Multiplatform → Funciona em Android, Web, Desktop, iOS

### Armazenamento
- **Atual:** Memória (StateFlow)
- **Recomendado para produção:**
  - Android: `EncryptedSharedPreferences`
  - iOS: `Keychain`
  - Web: `localStorage` com encriptação

---

## 🔒 Segurança

> ⚠️ O token é armazenado em memória, então:
> - Será perdido ao fechar o app (comportamento esperado)
> - Para persistência segura em produção, use as soluções recomendadas acima

---

## 🚀 Próximos Passos (Opcional)

1. **Armazenamento Persistente:** Implementar `EncryptedSharedPreferences` no Android
2. **Refresh Token:** Adicionar lógica para renovar o token automaticamente
3. **Token Expiration:** Extrair a data de expiração do JWT e fazer logout automático
4. **Logout Remoto:** Permitir logout automático se o servidor invalida o token

---

## 📞 Resumo das Funções do TokenManager

| Função | Descrição |
|--------|-----------|
| `setToken(token)` | Salva o token JWT |
| `getToken()` | Recupera o token atual |
| `getAuthorizationHeader()` | Retorna `"Bearer <token>"` |
| `clearToken()` | Limpa o token (logout) |
| `hasValidToken()` | Verifica se tem token válido |
| `token` (StateFlow) | Estado reativo do token |
| `isAuthenticated` (StateFlow) | Estado reativo da autenticação |

---

## ✅ Checklist de Implementação

- ✅ TokenManager criado
- ✅ AuthorizationHeaderPlugin implementado em ApiClient
- ✅ ApiAuthRepository salva o token após login
- ✅ App.kt limpa o token ao fazer logout
- ✅ Header Authorization adicionado automaticamente em todas as requisições
- ✅ Sem erros de compilação (apenas avisos sobre funções não usadas — isso é normal)


