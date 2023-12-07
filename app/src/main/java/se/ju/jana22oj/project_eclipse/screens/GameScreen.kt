package se.ju.jana22oj.project_eclipse.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.garrit.android.multiplayer.SupabaseService
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


    // Use the gameplayViewModel to collect states
    val isMyTurn by gameplayViewModel._isMyTurn.collectAsState()
    val isGameOver by gameplayViewModel._isGameOver.collectAsState()
    val gameResult by gameplayViewModel._gameResult.collectAsState()



    //val boardToDisplay =
    //derivedStateOf(isMyTurn)
    val boardToDisplay by remember {
        derivedStateOf {
            if (isMyTurn) gameplayViewModel.opponentBoard else gameplayViewModel.playerBoard
        }
    }

    if (isGameOver) {
        GameResultScreen(gameResult, navController)
    } else {
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
            GameBoardView(boardToDisplay, isMyTurn, gameplayViewModel)
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
    gameplayViewModel: GameplayViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(Board.BoardSize),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        /*items(Board.BoardSize * Board.BoardSize) { index ->
            val x = index % Board.BoardSize
            val y = index / Board.BoardSize
            val cell = board.getCell(Coordinates(x, y))

            GameCellView(cell, isMyTurn,) {
                if (isMyTurn) {
                    gameplayViewModel.attack(x, y)
                }
            }
        }*/
        items(board.cells) { cell ->
            val x = cell.coordinates.x
            val y = cell.coordinates.y

            GameCellView(cell, isMyTurn,) {
                if (isMyTurn) {
                    gameplayViewModel.attack(x, y)
                }
            }
        }
    }
}

@Composable
fun GameCellView(
    cell: Cell,
    isMyTurn: Boolean,
    onAttack: () -> Unit
) {
    val backgroundColor = when {
        cell.isHit() -> Color.Red
        cell.isMiss() -> Color.Blue
        else -> Color.LightGray
    }

    Button(
        onClick = {if (isMyTurn && cell.isAttackable()) onAttack()},
        modifier = Modifier
            .aspectRatio(1f)
            .border(1.dp, Color.Black)
            .background(backgroundColor)
            .padding(4.dp),
        enabled = isMyTurn && !cell.isHit() && !cell.isMiss(),
        contentPadding = PaddingValues(0.dp)
    ) {

    }
}
fun Cell.isAttackable() = !isHit() && !isMiss()