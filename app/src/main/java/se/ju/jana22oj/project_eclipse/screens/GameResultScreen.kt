package se.ju.jana22oj.project_eclipse.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.garrit.android.multiplayer.GameResult
import io.garrit.android.multiplayer.SupabaseService
import se.ju.jana22oj.project_eclipse.viewmodels.GameplayViewModel


@Composable
fun GameResultScreen(
    gameResult: GameResult?,
    navController: NavController,
    gameplayViewModel: GameplayViewModel
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Game Over",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Result: ${gameResult?.let { getResultText(it, gameplayViewModel) } ?: "Unknown"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { navController.navigate(route = Screen.Lobby.route) },
                colors = ButtonDefaults.buttonColors(Color.Blue)
            ) {
                Text("Return to Lobby", color = Color.Red)
            }
        }
    }
}

fun getResultText(gameResult: GameResult, gameplayViewModel: GameplayViewModel): String {
    return when (gameResult) {
        GameResult.WIN -> "You Won!"
        GameResult.LOSE -> "You Lost"
        GameResult.DRAW -> "It's a Draw"
        GameResult.SURRENDER -> if (gameplayViewModel.currentPlayer == SupabaseService.currentGame?.player1) "You Surrendered" else "Opponent Surrendered"
        else -> "Unknown"
    }
}


