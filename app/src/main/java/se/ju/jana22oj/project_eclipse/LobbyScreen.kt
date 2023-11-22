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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.garrit.android.multiplayer.Game
import io.garrit.android.multiplayer.Player
import io.garrit.android.multiplayer.SupabaseService


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen( modifier: Modifier = Modifier, navController: NavController, lobbyViewModel: LobbyViewModel = LobbyViewModel()){

    LaunchedEffect(true) {
        lobbyViewModel.joinLobby(Player(name = "Hej"))
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
        Button(onClick = { lobbyViewModel.invitePlayer(player) }) {
            Text("Text")

        }
    }
}




/*
@Preview(showBackground = true, heightDp = 320, widthDp = 320)
@Composable
fun DePreview(){

    LobbyScreen(navController = NavController(), lobbyViewModel = LobbyViewModel())
}

 */