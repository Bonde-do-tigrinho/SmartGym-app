package org.smartgym

sealed class Screen(val route: String){
    object Home : Screen("home")
    object Aparelhos: Screen("aparelhos")
    object Treino: Screen("treino")
    object Pagamentos: Screen("pagamentos")
}