package se.ju.jana22oj.project_eclipse

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.garrit.android.multiplayer.Player
import se.ju.jana22oj.project_eclipse.screens.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController = rememberNavController(), lobbyViewModel: LobbyViewModel = LobbyViewModel()){
    var playerName by remember { mutableStateOf("") }


    Box(modifier = Modifier.fillMaxSize()){
        Image(painter = painterResource(id = R.drawable.my_battlefield),
            contentDescription = "BackgroundImage",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize())
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Welcome to Call of Duty Battleships",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        TextField(
            value = playerName,
            onValueChange = { playerName = it },
            label = { Text("Enter your name") },
            modifier = Modifier.padding(vertical = 24.dp)
        )
        Button(
            modifier = Modifier
                .padding(vertical = 24.dp)
                .height(50.dp)
                .fillMaxWidth(fraction = 0.8f),
                onClick = {
                    lobbyViewModel.joinLobby(Player(name = playerName))
                    navController.navigate(route = Screen.Lobby.route)
                          },
                colors = ButtonDefaults.buttonColors(Color.Black)
            ) {
            Text("Start Game")

        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun DefaultPreview(){
    HomeScreen()
}