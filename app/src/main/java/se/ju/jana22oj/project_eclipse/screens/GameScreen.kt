package se.ju.jana22oj.project_eclipse.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.garrit.android.multiplayer.SupabaseService
import se.ju.jana22oj.project_eclipse.R
import se.ju.jana22oj.project_eclipse.viewmodels.Board
import se.ju.jana22oj.project_eclipse.viewmodels.Cell
import se.ju.jana22oj.project_eclipse.viewmodels.Coordinates
import se.ju.jana22oj.project_eclipse.viewmodels.GameplayViewModel
import se.ju.jana22oj.project_eclipse.viewmodels.GameplayViewModelFactory
import se.ju.jana22oj.project_eclipse.viewmodels.SetupShipViewModel
import se.ju.jana22oj.project_eclipse.viewmodels.Ship


@Composable
fun GameplayScreen( navController: NavController, setupShipViewModel: SetupShipViewModel,
                   supabaseService: SupabaseService) {
    val gameplayViewModel: GameplayViewModel = viewModel(
        factory = GameplayViewModelFactory(setupShipViewModel, supabaseService)
    )



    val isMyTurn by gameplayViewModel._isMyTurn.collectAsState()
    val isGameOver by gameplayViewModel._isGameOver.collectAsState()
    val gameResult by gameplayViewModel._gameResult.collectAsState()
    val ships = gameplayViewModel.ships
    val showShips = !isMyTurn



    val boardToDisplay by remember {
        derivedStateOf {
            if (isMyTurn) gameplayViewModel.opponentBoard else gameplayViewModel.playerBoard
        }
    }

    if (isGameOver) {
        GameResultScreen(gameResult, navController, gameplayViewModel)
    } else {
        Box(modifier = Modifier.fillMaxSize()){
            Image(painter = painterResource(id = R.drawable.gameplay_screen),
                contentDescription = "BackgroundImage",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize())
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isMyTurn) "YOUR TURN" else "OPPONENT'S TURN",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Displaying the game board
            GameBoardView(boardToDisplay, isMyTurn, ships,showShips, gameplayViewModel)
            Spacer(modifier = Modifier.weight(1f)) // Pushes the button to the bottom
            Button(
                onClick = { gameplayViewModel.playerSurrender() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text("SURRENDER", color = Color.White)
            }
        }
    }
}

@Composable
fun GameBoardView(
    board: Board,
    isMyTurn: Boolean,
    ships: List<Ship>,
    showShips: Boolean,
    gameplayViewModel: GameplayViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(Board.BoardSize),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(Board.BoardSize * Board.BoardSize) { index ->
            val x = index % Board.BoardSize
            val y = index / Board.BoardSize
            val cell = board.getCell(Coordinates(x, y))

            GameCellView(cell, isMyTurn, ships, showShips) {
                if (isMyTurn) {
                    gameplayViewModel.attack(x, y)
                }
            }
        }
    }
}

// my friend help  me with the next 10 lines to put ship icons on the button
@Composable
fun GameCellView(
    cell: Cell,
    isMyTurn: Boolean,
    ships: List<Ship>,
    showShips: Boolean,
    onAttack: () -> Unit
) {
    val shipInCell = if (showShips) ships.find { ship -> cell.coordinates in ship.coordinates } else null
    val backgroundColor = when {
        showShips && shipInCell?.isSunk() == true -> Color.Black
        cell.isHit() -> Color.Red
        cell.isMiss() -> Color.Blue
        else -> Color.LightGray
    }

    Button(
        onClick = { if (isMyTurn && cell.isAttackable()) onAttack() },
        modifier = Modifier
            .aspectRatio(1f)
            .border(1.dp, Color.Black)
            .background(backgroundColor)
            .padding(4.dp),
        enabled = isMyTurn && cell.isAttackable(),
        colors = ButtonDefaults.buttonColors(containerColor = if (shipInCell != null  && !showShips) Color.Transparent else backgroundColor),
        shape = RectangleShape,
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (shipInCell != null) {
                ShipIcon(shipInCell.type, Modifier.fillMaxSize())
            }
        }
    }
}

fun Cell.isAttackable(): Boolean {
    return !isHit() && !isMiss()
}