package se.ju.jana22oj.project_eclipse.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import se.ju.jana22oj.project_eclipse.viewmodels.Card
import se.ju.jana22oj.project_eclipse.viewmodels.GameViewModel

@Composable
fun GameScreen(gameViewModel: GameViewModel = viewModel(),navController: NavController = rememberNavController())
{
    val cards = gameViewModel.cards
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "GAME")
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
           items(cards){card ->
               CardView(card, onClick = {
                   gameViewModel.flipCard(card)
               })
           }
        }
    }
}

@Composable
fun CardView(card: Card, onClick: () -> Unit) {
    val isFlipped by card.isFlipped

    Button(onClick = onClick,
        modifier = Modifier.aspectRatio(1f),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
        shape = RectangleShape
        ) {
        if(isFlipped){
            Icon(imageVector = card.symbol, contentDescription = "Card symbol")
        }

    }
}