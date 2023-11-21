package se.ju.jana22oj.project_eclipse.screens

sealed class Screen(val route:String){
    object Main : Screen(route = "main")
    object Game : Screen(route = "game")


}
