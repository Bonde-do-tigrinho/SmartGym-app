<h1 align="center">
  🏋️‍♂️ SmartGym
</h1>

<p align="center">
  <b>Sistema Inteligente de Gestão e Monitoramento de Treinos</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Compose_Multiplatform-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose Multiplatform" />
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Windows-0078D6?style=for-the-badge&logo=windows&logoColor=white" alt="Windows" />
</p>

---

## 📝 Sobre o Projeto

O **SmartGym** é uma solução multiplataforma robusta desenvolvida para modernizar a gestão de academias. Através de uma arquitetura unificada, o sistema oferece interfaces específicas para alunos, professores e administradores, garantindo agilidade no acompanhamento de treinos e na administração de unidades.

Este software foi desenvolvido como **Trabalho de Conclusão de Curso (TCC)**, utilizando o ecossistema **Kotlin Multiplatform (KMP)**. O diferencial do projeto é o compartilhamento de lógica de negócio e interface entre diferentes sistemas operacionais, permitindo que o aplicativo rode nativamente tanto em dispositivos móveis (**Android**) quanto em computadores (**Windows Desktop**) com alta performance e fidelidade visual.

### ✨ Principais Funcionalidades
- **🏋️ Gestão de Treinos:** Fichas digitais dinâmicas com foco em hipertrofia.
- **⚡ Monitoramento em Tempo Real:** Visualização de ocupação de aparelhos.
- **📊 Painel Administrativo:** Controle de unidades, alunos e métricas financeiras.
- **🎨 Interface Adaptável:** Design moderno com suporte nativo a Temas Claros e Escuros.

---

## 👥 Integrantes (Grupo SmartGym)

O projeto foi idealizado e desenvolvido com dedicação pela equipe:

| Desenvolvedor | Perfil no GitHub |
| :--- | :--- |
| **Raul** | [![GitHub](https://img.shields.io/badge/GitHub-100000?style=flat&logo=github&logoColor=white)](https://github.com/R4ULz) |
| **Leandro** | [![GitHub](https://img.shields.io/badge/GitHub-100000?style=flat&logo=github&logoColor=white)](https://github.com/LeandroRodrigues061) |
| **Gabriel** | [![GitHub](https://img.shields.io/badge/GitHub-100000?style=flat&logo=github&logoColor=white)](https://github.com/Kendi9866) |
| **Nicolas** | [![GitHub](https://img.shields.io/badge/GitHub-100000?style=flat&logo=github&logoColor=white)](https://github.com/Nick-Yanase) |
| **Miguel** | [![GitHub](https://img.shields.io/badge/GitHub-100000?style=flat&logo=github&logoColor=white)](https://github.com/Bzats) |
| **Joao** | [![GitHub](https://img.shields.io/badge/GitHub-100000?style=flat&logo=github&logoColor=white)](https://github.com/MaximianoJ) |

---

## 🛠 Tecnologias Utilizadas

* **Linguagem:** Kotlin
* **UI Framework:** Compose Multiplatform
* **Navegação:** JetBrains Navigation (Multiplatform)
---

## 🚀 Instalação e Execução

### Pré-requisitos
Para compilar e rodar o projeto, você precisará de:
* **JDK 17** ou superior.
* **Android Studio** (Hedgehog ou superior) ou **IntelliJ IDEA / Fleet**.
* Android SDK configurado (para a versão mobile).

### Passo a Passo

**1. Clonar o Repositório:**
```bash
git clone [https://github.com/Bonde-do-tigrinho/SmartGym-app.git]
cd SmartGym-App
```

**2. Executar no Windows (Desktop):**
```bash
./gradlew :composeApp:run
```

**3. Executar no Android (Mobile):**
```bash
./gradlew :composeApp:installDebug
```

**Após o comando terminar com sucesso (BUILD SUCCESSFUL), abra os aplicativos do celular/emulador e clique no ícone do SmartGym para iniciar.**