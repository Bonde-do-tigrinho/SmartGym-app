package org.smartgym

sealed class Screen(val route: String){

    //Rotas do aluno
    object HomeAluno : Screen("home_aluno")
    object Aparelhos: Screen("aparelhos")
    object Treino: Screen("treino")
    object Pagamentos: Screen("pagamentos")
    object PerfilAluno : Screen("perfil_aluno")

    //Rotas do professor
    object HomeProfessor : Screen("home_professor")
    object Exercicios : Screen("exercicios")
    object NovoExercicio : Screen("novo_exercicio")
    object Fichas : Screen("fichas")
    object Avaliacoes : Screen("avaliacoes")
    object NovaAvaliacao : Screen("nova_avaliacao")

    //Rotas do admin
    object HomeAdmin: Screen("home_admin")
    object UnidadesAdmin : Screen("unidades_admin")
    object MaquinasAdmin : Screen("maquinas_admin")
    object AlunosAdmin: Screen("alunos_admin")
    object NovoAluno : Screen("novo_aluno")
    object EditarAluno : Screen("editar_aluno")

}