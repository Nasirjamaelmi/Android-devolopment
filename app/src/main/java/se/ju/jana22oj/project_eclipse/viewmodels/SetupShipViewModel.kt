package se.ju.jana22oj.project_eclipse.viewmodels


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.garrit.android.multiplayer.SupabaseService
import kotlinx.coroutines.launch
import se.ju.jana22oj.project_eclipse.viewmodels.Board.Companion.BoardSize

//my friend helped me with the board and cell class
class Ship(val type: ShipType, val coordinates: List<Coordinates>){
    var _isSunk = false

    fun isSunk(): Boolean {
        return _isSunk
    }

    fun markSunk() {
        _isSunk = true
    }
}
data class Coordinates(val x: Int, val y: Int)
enum class ShipType(val size: Int) {
    CARRIER(4),
    BATTLESHIP(3),
    CRUISER(2),
    DESTROYER(1)
}

class Board {
    companion object {
        const val BoardSize = 10
    }
    private val _cells = mutableStateListOf<Cell>()
    val cells: SnapshotStateList<Cell>
        get() = _cells

    init {
        _cells.clear()
        for (x in 0 until BoardSize) {
            for (y in 0 until BoardSize) {
                _cells.add(Cell(Coordinates(x, y)))
            }
        }
    }

    fun placeShip(ship: Ship) {
        for (coordinate in ship.coordinates) {
            val cell = getCell(coordinate)
            cell.occupy(ship)
        }
    }

    fun removeShip(ship: Ship) {
        for (coordinate in ship.coordinates) {
            val cell = getCell(coordinate)
            cell.clear()
        }
    }

    fun getCell(coordinate: Coordinates): Cell {
        for (cell in _cells) {
            if (cell.coordinates == coordinate) {
                return cell
            }
        }
        throw Exception("Invalid coordinate: $coordinate")
    }

    fun isCellOccupied(coordinate: Coordinates): Boolean {
        return _cells.any { cell -> cell.coordinates == coordinate && cell.isOccupied() }
    }
}



class Cell(val coordinates: Coordinates) {
    private var _occupant: Ship? = null
    private var _isHit = false
    private var _isMiss = false

    fun occupy(ship: Ship) {
        _occupant = ship
    }

    fun clear() {
        _occupant = null
    }

    fun isOccupied(): Boolean {
        return _occupant != null
    }

    // when a cell is hit by an attack
    fun markHit() {
        _isHit = true

    }

    // Call this method when an attack on a cell is a miss
    fun markMiss() {
        _isMiss = true
    }

    // Check if the cell has been hit
    fun isHit(): Boolean {
        return _isHit
    }

    // Check if there was a miss on the cell
    fun isMiss(): Boolean {
        return _isMiss
    }
}





class SetupShipViewModel: ViewModel() {
    val _ships: SnapshotStateList<Ship> = mutableStateListOf<Ship>()
    val ships: SnapshotStateList<Ship> = _ships
    val board = Board()
    val availabeshipTypes = mutableStateListOf(
        ShipType.CARRIER,
        ShipType.BATTLESHIP,
        ShipType.CRUISER, ShipType.CRUISER,
        ShipType.DESTROYER, ShipType.DESTROYER
    )
    var isSetupComplete by mutableStateOf(false)
        private set


    fun placeShip(shipType: ShipType, coordinates: Coordinates, isRotated: Boolean) {


        // Validate ship placement (within board boundaries, no overlapping ships)
        if (!isValidPlacement(shipType, coordinates, isRotated)) {
            return
        }
        // Create a Ship object with the specified type and coordinates
        val ship = Ship(shipType, calculateShipCoordinates(shipType, coordinates, isRotated))

        // Place the ship on the board (update board state)
        board.placeShip(ship)
        // Add the ship to the _ships list
        _ships.add(ship)
        availabeshipTypes.remove(shipType)
        if (availabeshipTypes.isEmpty()) {
            isSetupComplete = true
        }

    }


    fun removeShip(ship: Ship) {
        // Remove the ship from the _ships list
        _ships.remove(ship)

        // Remove the ship from the board (update board state)
        board.removeShip(ship)

    }

    private fun calculateShipCoordinates(
        shipType: ShipType,
        startCoordinate: Coordinates,
        isRotated: Boolean
    ): List<Coordinates> {
        return if (isRotated) {
            // For horizontal placement
            (0 until shipType.size).map { xOffset ->
                Coordinates(startCoordinate.x + xOffset, startCoordinate.y)
            }
        } else {
            // For vertical placement (original implementation)
            (0 until shipType.size).map { yOffset ->
                Coordinates(startCoordinate.x, startCoordinate.y + yOffset)
            }
        }
    }


    fun isWithinBoardBoundaries(coordinates: Coordinates): Boolean {
        val (x, y) = coordinates
        return x in 0 until Board.BoardSize && y in 0 until BoardSize
    }

    fun canPlaceShip(existingShip: Ship, coordinates: Coordinates, newShipType: ShipType): Boolean {
        val (x, y) = coordinates

        // Check if the new ship is overlapping an existing ship
        for (existingShipCoordinate in existingShip.coordinates) {
            if (x == existingShipCoordinate.x && y == existingShipCoordinate.y) {
                return false
            }
        }

        // Check if the new ship can be placed without extending beyond the board boundaries
        val extendedShipCoordinates = mutableListOf<Coordinates>()
        for (yOffset in 0 until newShipType.size) {
            extendedShipCoordinates.add(Coordinates(x, y + yOffset))
        }

        return extendedShipCoordinates.none { existingShipCoordinate ->
            existingShipCoordinate in existingShip.coordinates
        }
    }


    fun startGame() {
        viewModelScope.launch {
            SupabaseService.playerReady()
        }
    }

    fun isValidPlacement(
        shipType: ShipType,
        coordinates: Coordinates,
        isRotated: Boolean
    ): Boolean {
        val shipCoordinates = calculateShipCoordinates(shipType, coordinates, isRotated)

        // Check if all coordinates of the ship are within the board boundaries
        if (shipCoordinates.any { !isWithinBoardBoundaries(it) }) {
            return false
        }

        // Check if the ship can be placed without overlapping existing ships
        for (shipCoord in shipCoordinates) {
            if (!canPlaceAtCoord(shipCoord)) {
                return false
            }
        }

        return true
    }


    fun canPlaceAtCoord(coord: Coordinates): Boolean {
        // Check if the cell is occupied
        if (board.isCellOccupied(coord)) {
            return false
        }

        // Check surrounding cells
        val surroundingCoords = listOf(
            Coordinates(coord.x - 1, coord.y), Coordinates(coord.x + 1, coord.y),
            Coordinates(coord.x, coord.y - 1), Coordinates(coord.x, coord.y + 1),
            Coordinates(coord.x - 1, coord.y - 1), Coordinates(coord.x + 1, coord.y + 1),
            Coordinates(coord.x - 1, coord.y + 1), Coordinates(coord.x + 1, coord.y - 1)
        )

        return surroundingCoords.all {
            !board.isCellOccupied(it)
        }
    }


}





