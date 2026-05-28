package org.smartgym

sealed class Screen(val route: String){

    //Rotas do aluno
    object HomeAluno : Screen("home_aluno")
    object Aparelhos: Screen("aparelhos")
    object Treino: Screen("treino")
    object Pagamentos: Screen("pagamentos")
    object PerfilAluno : Screen("perfil_aluno")
    object AulasAluno : Screen("aulas_aluno")

    //Rotas do professor
    object HomeProfessor : Screen("home_professor")
    object Exercicios : Screen("exercicios")
    object NovoExercicio : Screen("novo_exercicio")
    object Fichas : Screen("fichas")
    object NovaFicha : Screen("nova_ficha")
    object EditarFicha : Screen("editar_ficha")
    object Avaliacoes : Screen("avaliacoes")
    object NovaAvaliacao : Screen("nova_avaliacao")
    object AulasProfessor : Screen("aulas_professor")
    object UpsertAulaProfessor : Screen("upsert_aula_professor")

    //Rotas do admin
    object HomeAdmin: Screen("home_admin")
    object UnidadesAdmin : Screen("unidades_admin")
    object MaquinasAdmin : Screen("maquinas_admin")
    object MaquinasIotAdmin : Screen("maquinas_iot_admin")
    object AlunosAdmin: Screen("alunos_admin")
    object NovoAluno : Screen("novo_aluno")
    object EditarAluno : Screen("editar_aluno")
    object ProfessoresAdmin : Screen("professores_admin")
    object NovoProfessor : Screen("novo_professor")
    object EditarProfessor : Screen("editar_professor")

    object Notificacoes : Screen("notificacoes")
    object NovaNotificacao : Screen("nova_notificacao")
    object EditarNotificacao : Screen("editar_notificacao/{id}") {
        fun createRoute(id: Int) = "editar_notificacao/$id"
    }

}