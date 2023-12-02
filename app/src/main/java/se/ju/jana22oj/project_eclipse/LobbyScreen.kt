@file:OptIn(ExperimentalMaterial3Api::class)

package se.ju.jana22oj.project_eclipse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.Navigation
import androidx.navigation.compose.rememberNavController
import io.garrit.android.multiplayer.Game
import io.garrit.android.multiplayer.Player
import io.garrit.android.multiplayer.SupabaseService
import se.ju.jana22oj.project_eclipse.screens.Screen
import java.time.format.TextStyle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen( modifier: Modifier = Modifier, navController: NavController, lobbyViewModel: LobbyViewModel = LobbyViewModel()){

     if(lobbyViewModel.server.collectAsState().value.toString() == "GAME")
     {
         navController.navigate(Screen.Setup.route)
     }
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Lobby") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp()  }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        println("hej")
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) {innerPadding->
        LazyColumn(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ){
            items(lobbyViewModel.players) { player ->
                PlayerItem(player = player, lobbyViewModel = lobbyViewModel)
            }
            items(lobbyViewModel.games) { game ->
                GameItem(game = game, lobbyViewModel = lobbyViewModel ,navController = navController)
            }

        }
    }
}

@Composable
fun PlayerItem(player: Player, lobbyViewModel: LobbyViewModel){
    Row(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = player.name)
        Button(onClick = { lobbyViewModel.invitePlayer(player)}) {
            Text("Invite")

        }
    }
}

@Composable
fun GameItem(game: Game, lobbyViewModel: LobbyViewModel,  navController: NavController)  {

    Row {
        Text("${game.player1.name} has invited you")
        Button(
            onClick = {
                lobbyViewModel.acceptInvite(game)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        )
        {

            Text(
                text = "Accept",
                style = androidx.compose.ui.text.TextStyle(Color.Black),
                fontWeight = FontWeight.Bold
            )
        }
        Button(
            onClick = {
                lobbyViewModel.declineInvite(game)

            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        )
        {
            Text(
                text = "Decline",
                style = androidx.compose.ui.text.TextStyle(Color.White),
                fontWeight = FontWeight.Bold
            )
        }
    }
}


