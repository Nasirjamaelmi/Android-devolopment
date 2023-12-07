package se.ju.jana22oj.project_eclipse

import android.os.Bundle
import android.widget.GridLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import se.ju.jana22oj.project_eclipse.ui.theme.ProjecteclipseTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import se.ju.jana22oj.project_eclipse.screens.GameplayScreen
import se.ju.jana22oj.project_eclipse.screens.MainScreen
import se.ju.jana22oj.project_eclipse.screens.Screen
import se.ju.jana22oj.project_eclipse.screens.SetupShipScreen
import se.ju.jana22oj.project_eclipse.viewmodels.SetupShipViewModel
import io.garrit.android.multiplayer.SupabaseService


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val setupShipViewModel = SetupShipViewModel() // Initialize with necessary parameters if any
            val supabaseService = SupabaseService // Initialize with necessary parameters if any

            ProjecteclipseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //HomeScreen()
                   // GameBoard()
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

