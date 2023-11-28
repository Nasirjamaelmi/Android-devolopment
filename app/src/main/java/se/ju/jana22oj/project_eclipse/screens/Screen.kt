package se.ju.jana22oj.project_eclipse.screens

sealed class Screen(val route:String){
    object Main : Screen(route = "main")
    object Setup: Screen(route = "Setup")
    object Lobby: Screen(route = "lobby")
    object Game : Screen(route = "game")


}
