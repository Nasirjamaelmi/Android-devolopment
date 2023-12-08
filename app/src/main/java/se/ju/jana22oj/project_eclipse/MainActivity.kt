package se.ju.jana22oj.project_eclipse
//https://chat.openai.com/share/2781d05c-5313-429b-93cb-c978f21c0e2c
//https://chat.openai.com/share/76fd13c3-637d-4330-849d-1e7e20ad7a16
//https://chat.openai.com/share/e586663c-34b5-4d53-b8d5-1b349dc4fe75

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import se.ju.jana22oj.project_eclipse.ui.theme.ProjecteclipseTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import se.ju.jana22oj.project_eclipse.screens.GameplayScreen
import se.ju.jana22oj.project_eclipse.screens.Screen
import se.ju.jana22oj.project_eclipse.screens.SetupShipScreen
import se.ju.jana22oj.project_eclipse.viewmodels.SetupShipViewModel
import io.garrit.android.multiplayer.SupabaseService


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val setupShipViewModel = SetupShipViewModel()
            val supabaseService = SupabaseService

            ProjecteclipseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = Screen.Main.route) {
                        composable(route = Screen.Main.route){
                            HomeScreen(navController = navController)
                        }
                        composable(route = Screen.Lobby.route){
                            LobbyScreen(navController = navController)
                        }
                        composable(route = Screen.Setup.route){
                            SetupShipScreen(setupShipViewModel = setupShipViewModel,navController = navController)
                        }
                        composable(route = Screen.Game.route) {
                            GameplayScreen(
                                navController = navController,
                                setupShipViewModel = setupShipViewModel,
                                supabaseService = supabaseService
                            )
                        }
                    }
                }
            }
        }
    }
}

