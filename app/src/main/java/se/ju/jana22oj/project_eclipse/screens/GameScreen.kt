package se.ju.jana22oj.project_eclipse.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import se.ju.jana22oj.project_eclipse.R
import se.ju.jana22oj.project_eclipse.viewmodels.Cell
import se.ju.jana22oj.project_eclipse.viewmodels.GameViewModel


@Composable
fun GameScreen(gameViewModel: GameViewModel = viewModel(),navController: NavController = rememberNavController())
{
    val cells = gameViewModel.cells
    val shipCreationMessage = gameViewModel.shipCreationMessage


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Your Board")
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(10),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        )
        {
           items(cells ){cell ->
               CellView(cell, onClick = {
                    gameViewModel.selectCell(cell)
               })
           }
            
        }
        Text(
            text = buildAnnotatedString {
                val delimiter = "<br>"
                val parts = shipCreationMessage.value.split(delimiter)
                for ((index, part) in parts.withIndex()) {
                    append(part)
                    if (index < parts.size - 1) {
                        append("\n")
                    }
                }
            },
            modifier = Modifier
                .padding(top = 20.dp)
        )
    }

}

@Composable
fun CellView(cell: Cell, onClick: () -> Unit) {
    Button(
        onClick = {
            onClick()
        },
        modifier = Modifier.aspectRatio(1f),
        colors = ButtonDefaults.buttonColors(containerColor = cell.color.value),
        shape = RectangleShape
    ) {
        // Add content if needed
    }
}
