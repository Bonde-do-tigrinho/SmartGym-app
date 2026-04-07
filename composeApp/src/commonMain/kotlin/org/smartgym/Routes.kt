package org.smartgym

sealed class Screen(val route: String){

    //Rotas do aluno
    object HomeAluno : Screen("home_aluno")
    object Aparelhos: Screen("aparelhos")
    object Treino: Screen("treino")
    object Pagamentos: Screen("pagamentos")

    //Rotas do professor
    object HomeProfessor : Screen("home_professor")

    //Rotas do admin
    object HomeAdmin: Screen("home_admin")
    object AlunosAdmin: Screen("alunos_admin")
}