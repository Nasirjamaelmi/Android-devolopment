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

class GameplayViewModel(setupShipViewModel: SetupShipViewModel, supabaseService: SupabaseService) : ViewModel(),
    SupabaseCallback {
    val _shipSetupViewModel = setupShipViewModel
    var playerBoard = Board() // For the player's board
    var opponentBoard = Board()
    val _isMyTurn = MutableStateFlow(false)
    val _opponentShipCoordinates = mutableStateListOf<Coordinates>()
    val _isGameOver = MutableStateFlow(false)
    val _gameResult = MutableStateFlow<GameResult?>(null)
    val ships: SnapshotStateList<Ship> = _shipSetupViewModel.ships
    var lastAttackCoordinates: Coordinates? = null
    val isOpponentReady = MutableStateFlow(false)
    var currentPlayer by mutableStateOf<Player?>(SupabaseService.currentGame?.player1)


    init {
        playerBoard = _shipSetupViewModel.board
        //_shipSetupViewModel.startGame()
        supabaseService.callbackHandler = this
        if (currentPlayer == supabaseService.currentGame?.player1) {
            _isMyTurn.value = true
        }

    }

    override suspend fun playerReadyHandler() {
        isOpponentReady.value = true
        if (_shipSetupViewModel.isSetupComplete) {
            //_shipSetupViewModel.startGame() // Call startGame from SetupShipViewModel
        }
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
                ActionResult.HIT -> cell.markHit(opponentBoard)
                ActionResult.MISS -> cell.markMiss()
                ActionResult.SUNK -> cell.markHit(opponentBoard)
            }
            lastAttackCoordinates = null
        }
        _isMyTurn.value = (status == ActionResult.HIT)
        checkWinCondition()
    }

    override suspend fun finishHandler(status: GameResult) {
        // Handle finish event
        _isGameOver.value = true
        _gameResult.value = status
    }


    fun handleOpponentAttack(x: Int, y: Int) {
        val coordinate = Coordinates(x, y)
        val cell = playerBoard.getCell(coordinate)
        viewModelScope.launch {
            if (cell.isOccupied()) {
                cell.markHit(playerBoard) // This will also check and mark the cell as sunk if needed
                SupabaseService.sendAnswer(if (cell.isSunk()) ActionResult.SUNK else ActionResult.HIT)
            } else {
                cell.markMiss()
                SupabaseService.sendAnswer(ActionResult.MISS)
            }
            checkWinCondition()
        }
    }

/*
    fun updateShipStatus(hitCoordinate: Coordinates): Boolean {
        // Find the ship that has been hit
        ships.find { ship -> hitCoordinate in ship.coordinates }?.let { hitShip ->
            // Check if all parts (cells) of the ship are sunk
            val isSunk = hitShip.coordinates.all { coord -> playerBoard.getCell(coord).isSunk() }
            if (isSunk) {
                // If all parts are sunk, mark the entire ship as sunk

                return true
            }
        }
        // No need to call checkWinCondition here as it's already being called after each attack
        return false
    }

*/
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

    fun allOpponentShipsSunk(): Boolean {
        // Check if all opponent's ships are sunk
        return _opponentShipCoordinates.all { coordinate ->
            val cell = opponentBoard.getCell(coordinate)
            cell.isSunk()
        }
    }

    private fun allPlayerShipsSunk(): Boolean {
        return playerBoard.getAllCells().all { cell ->
            !cell.isOccupied() || cell.isSunk()
        }
    }

    private fun checkWinCondition() {
        if (!_shipSetupViewModel.isSetupComplete) return
        when {
            allOpponentShipsSunk() -> gameFinish(GameResult.WIN)
            allPlayerShipsSunk() -> gameFinish(GameResult.LOSE)
        }
    }



    private fun gameFinish(result: GameResult ) {
        _gameResult.value = result
        _isGameOver.value = true
        viewModelScope.launch {
            SupabaseService.gameFinish(result)
        }
    }

    fun playerSurrender() {
        gameFinish(GameResult.SURRENDER)
    }




}
