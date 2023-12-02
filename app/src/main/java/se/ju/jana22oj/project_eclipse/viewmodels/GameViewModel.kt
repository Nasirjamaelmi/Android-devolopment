package se.ju.jana22oj.project_eclipse.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.garrit.android.multiplayer.ActionResult
import io.garrit.android.multiplayer.GameEventType
import io.garrit.android.multiplayer.GameResult
import io.garrit.android.multiplayer.SupabaseService
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class GameplayViewModel(setupShipViewModel: SetupShipViewModel, supabaseClient: SupabaseClient) : ViewModel()  {
    val _shipSetupViewModel = SetupShipViewModel()
    val _board = _shipSetupViewModel.board
    val _isMyTurn = MutableStateFlow(false)
    val _opponentShipCoordinates = mutableStateListOf<Coordinates>()
    val _isGameOver = MutableStateFlow(false)
    val _gameResult = MutableStateFlow<GameResult?>(null)

    val ships: SnapshotStateList<Ship> = _shipSetupViewModel.ships
    val board: Board = _board
    val isMyTurn: StateFlow<Boolean> = _isMyTurn.asStateFlow()
    val opponentShipCoordinates: List<Coordinates> = _opponentShipCoordinates
    val isGameOver: StateFlow<Boolean> = _isGameOver.asStateFlow()
    val gameResult: StateFlow<GameResult?> = _gameResult.asStateFlow()

    fun handleOpponentAttack(x: Int, y: Int) {
        if (!_isMyTurn.value) {
            val coordinate = Coordinates(x, y)
            val cell = _board.getCell(coordinate)

            viewModelScope.launch {
                if (cell.isOccupied()) {
                    // Hit
                    SupabaseService.sendAnswer(ActionResult.HIT)
                    updateShipStatus(coordinate)
                } else {
                    // Miss
                    SupabaseService.sendAnswer(ActionResult.MISS)
                }
            }
        }
    }

    private fun updateShipStatus(hitCoordinate: Coordinates) {
        // Logic to update ship status and check if a ship is sunk
        ships.find { ship -> hitCoordinate in ship.coordinates }?.let { hitShip ->
            val isSunk = hitShip.coordinates.all { _board.getCell(it).isHit() }
            if (isSunk) {
                // Handle sunk ship
            }
        }
    }

    fun attack(x: Int, y: Int) {
        if (_isMyTurn.value) {
            val coordinate = Coordinates(x, y)

            viewModelScope.launch {
                if (coordinate in _opponentShipCoordinates) {
                    // Hit
                    SupabaseService.sendTurn(x, y)
                    // Additional logic for hit
                } else {
                    // Miss
                    SupabaseService.sendTurn(x, y)
                    // Additional logic for miss
                }
                _isMyTurn.value = false // Toggle turn
            }
        }
    }

    fun handleGameStateUpdates() {
        viewModelScope.launch {
            while (!_isGameOver.value) {
                val event = SupabaseService.gameEventChannel.receive()

                when (event.type) {
                    GameEventType.ACTION -> {
                        val (x, y) = event.data
                        handleOpponentAttack(x, y)
                    }

                    GameEventType.ANSWER -> {
                        val status = ActionResult.values().getOrNull(event.data.first())
                        if (status != null) {
                            // Update board UI based on opponent's response
                        }
                    }

                    GameEventType.FINISH -> {
                        val result = GameResult.values().getOrNull(event.data.first())
                        if (result != null) {
                            _isGameOver.value = true
                            _gameResult.value = result
                            // Perform any end-of-game logic here
                        }
                    }
                }
            }
        }
    }

    init {
        _shipSetupViewModel.startGame()
        handleGameStateUpdates()
    }
}
