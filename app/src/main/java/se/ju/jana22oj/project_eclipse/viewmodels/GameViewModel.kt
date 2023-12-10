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
import kotlinx.coroutines.delay
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
        supabaseService.callbackHandler = this


    }
    override suspend fun playerReadyHandler() {
        isOpponentReady.value = true
        checkAndStartGame()
    }

    private fun checkAndStartGame() {
        if (_shipSetupViewModel.isSetupComplete && isOpponentReady.value) {
            startGame()
        }
    }

    private fun startGame() {

        determineInitialTurn()
    }

    private fun determineInitialTurn() {
       _isMyTurn.value = (currentPlayer == SupabaseService.currentGame?.player1)
    }

    override suspend fun releaseTurnHandler() {
        delay(1500)
        _isMyTurn.value = true
    }

    override suspend fun actionHandler(x: Int, y: Int) {
        handleOpponentAttack(x,y)

    }

   //when the player receives the result of their attack
   override suspend fun answerHandler(status: ActionResult) {
       lastAttackCoordinates?.let { coords ->
           val cell = opponentBoard.getCell(coords)
           when (status) {
               ActionResult.HIT, ActionResult.SUNK -> {
                   cell.markHit()
                   _isMyTurn.value = true // Player continues their turn
               }
               ActionResult.MISS -> {
                   cell.markMiss()
                   _isMyTurn.value = false // Turn ends, opponent's turn
                   SupabaseService.releaseTurn()
               }
           }
           lastAttackCoordinates = null
       }
   }


    override suspend fun finishHandler(status: GameResult) {
        val finalResult = if (status == GameResult.WIN && currentPlayer != SupabaseService.currentGame?.player1) {
            GameResult.LOSE
        } else if (status == GameResult.LOSE && currentPlayer == SupabaseService.currentGame?.player1) {
            GameResult.WIN
        } else {
            status
        }

        _isGameOver.value = true
        _gameResult.value = finalResult
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
                checkWinCondition()
            }
        }
    }

    fun updateShipStatus(hitCoordinate: Coordinates): Boolean {
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
            }
        }
    }



    fun allPlayerShipsSunk(): Boolean {
        // Check if all player's ships are sunk
        return ships.all { ship -> ship.isSunk() }
    }

    private fun checkWinCondition() {
        if(!_shipSetupViewModel.isSetupComplete) return
        if (allPlayerShipsSunk()) {
            gameFinish(GameResult.LOSE)
        }
    }

    private fun gameFinish(result: GameResult) {
        val finalResult = if (result == GameResult.SURRENDER) {
            if (currentPlayer == SupabaseService.currentGame?.player1) GameResult.LOSE else GameResult.WIN
        } else {
            result
        }

        _gameResult.value = finalResult
        _isGameOver.value = true
        viewModelScope.launch {
            SupabaseService.gameFinish(finalResult)
        }
    }


    var isPlayerSurrendered = false
        private set
    fun playerSurrender() {
        isPlayerSurrendered = true
        gameFinish(GameResult.SURRENDER)
    }

    fun onLeavegame(){
        viewModelScope.launch {
            SupabaseService.leaveGame()
            SupabaseService.joinLobby(SupabaseService.player!!)
        }
    }

}
