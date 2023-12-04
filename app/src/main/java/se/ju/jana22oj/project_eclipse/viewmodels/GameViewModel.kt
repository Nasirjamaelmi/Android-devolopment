package se.ju.jana22oj.project_eclipse.viewmodels


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.garrit.android.multiplayer.ActionResult
import io.garrit.android.multiplayer.GameResult
import io.garrit.android.multiplayer.Player
import io.garrit.android.multiplayer.SupabaseCallback
import io.garrit.android.multiplayer.SupabaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GameplayViewModelFactory(
    private val setupShipViewModel: SetupShipViewModel,
    private val supabaseService: SupabaseService
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameplayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameplayViewModel(setupShipViewModel, supabaseService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class GameplayViewModel(setupShipViewModel: SetupShipViewModel, val supabaseService: SupabaseService) : ViewModel(),
    SupabaseCallback {

    val playerBoard = Board() // For the player's board
    val opponentBoard = Board()
    val _isMyTurn = MutableStateFlow(false)
    val _opponentShipCoordinates = mutableStateListOf<Coordinates>()
    val _isGameOver = MutableStateFlow(false)
    val _gameResult = MutableStateFlow<GameResult?>(null)
    val ships = setupShipViewModel.ships
    var lastAttackCoordinates: Coordinates? = null
    val isOpponentReady = MutableStateFlow(false)
    var currentPlayer by mutableStateOf<Player?>(SupabaseService.currentGame?.player1)




    override suspend fun playerReadyHandler() {
        isOpponentReady.value = true
    }


    override suspend fun releaseTurnHandler() {
        _isMyTurn.value = true
    }

    override suspend fun actionHandler(x: Int, y: Int) {
        handleOpponentAttack(x, y)

    }

    override suspend fun answerHandler(status: ActionResult) {
        lastAttackCoordinates?.let { coords ->
            val cell = opponentBoard.getCell(coords)
            when (status) {
                ActionResult.HIT -> {
                    cell.markHit()
                    updateShipStatus(coords) // Check if the ship is sunk
                }
                ActionResult.MISS -> cell.markMiss()
                ActionResult.SUNK -> {
                    // Here, the ship is already confirmed to be sunk
                    cell.markHit() // The cell is still hit
                    updateShipStatus(coords) // Update the status to sunk
                }
            }
            lastAttackCoordinates = null
        }
        _isMyTurn.value = (status == ActionResult.HIT)
    }

    override suspend fun finishHandler(status: GameResult) {
        // Handle finish event
        _isGameOver.value = true
        _gameResult.value = status
    }








    fun handleOpponentAttack(x: Int, y: Int) {
        if (!_isMyTurn.value) {
            val coordinate = Coordinates(x, y)
            val cell = playerBoard.getCell(coordinate)

            viewModelScope.launch {
                if (cell.isOccupied()) {
                    cell.markHit()
                    val isSunk = updateShipStatus(coordinate)
                    if (isSunk) {
                        SupabaseService.sendAnswer(ActionResult.SUNK)
                    } else {
                        SupabaseService.sendAnswer(ActionResult.HIT)
                    }
                } else {
                    cell.markMiss()
                    SupabaseService.sendAnswer(ActionResult.MISS)
                }
            }
        }
    }

     suspend fun updateShipStatus(hitCoordinate: Coordinates): Boolean {
        // Logic to update ship status and check if a ship is sunk
        ships.find { ship -> hitCoordinate in ship.coordinates }?.let { hitShip ->
            val isSunk = hitShip.coordinates.all { playerBoard.getCell(it).isHit() }
            if (isSunk) {
                hitShip.markSunk()
                return true
            }
        }
         checkWinCondition()
         return false
    }

    fun attack(x: Int, y: Int) {
        if (_isMyTurn.value) {
            val coordinate = Coordinates(x, y)
            lastAttackCoordinates = coordinate // Store the attack coordinates

            viewModelScope.launch {
                SupabaseService.sendTurn(x, y)
                _isMyTurn.value = false // Toggle turn
                SupabaseService.releaseTurn()
            }
        }
    }

    private fun allOpponentShipsSunk(): Boolean {
        // Check if all opponent's ships are sunk
        return _opponentShipCoordinates.all { coordinate ->
            val cell = opponentBoard.getCell(coordinate)
            cell.isHit() && cell.isOccupied() // Assuming a cell is marked hit and occupied if a ship is sunk
        }
    }

    private fun allPlayerShipsSunk(): Boolean {
        // Check if all player's ships are sunk
        return ships.all { ship -> ship.isSunk() }
    }

    private fun checkWinCondition() {
        when {
            // Assuming you have a method to check if all opponent's ships are sunk
            allOpponentShipsSunk() -> gameFinish(GameResult.WIN)
            // Assuming you have a method to check if all player's ships are sunk
            allPlayerShipsSunk() -> gameFinish(GameResult.LOSE)
            // Implement any draw condition if applicable
            // ...
        }
    }

    private fun gameFinish(result: GameResult ) {
        _gameResult.value = result
        _isGameOver.value = true
        viewModelScope.launch {
            SupabaseService.gameFinish(result)
        }
        // Additional logic like navigating to a result screen
    }

    fun playerSurrender() {
        gameFinish(GameResult.SURRENDER)
    }




    init {
        setupShipViewModel.ships.forEach { ship ->
            playerBoard.placeShip(ship)
        }
        supabaseService.callbackHandler = this
        if (setupShipViewModel.isSetupComplete) {
            setupShipViewModel.startGame()
        }
    }
}
