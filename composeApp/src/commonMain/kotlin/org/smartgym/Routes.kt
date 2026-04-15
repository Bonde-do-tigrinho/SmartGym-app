package org.smartgym

sealed class Screen(val route: String){

    //Rotas do aluno
    object HomeAluno : Screen("home_aluno")
    object Aparelhos: Screen("aparelhos")
    object Treino: Screen("treino")
    object Pagamentos: Screen("pagamentos")

    //Rotas do professor
    object HomeProfessor : Screen("home_professor")
    object Exercicios : Screen("exercicios")
    object Fichas : Screen("fichas")
    object Avaliacoes : Screen("avaliacoes")

    //Rotas do admin
    object HomeAdmin: Screen("home_admin")

    object UnidadesAdmin : Screen("unidades_admin")
    object AlunosAdmin: Screen("alunos_admin")
}