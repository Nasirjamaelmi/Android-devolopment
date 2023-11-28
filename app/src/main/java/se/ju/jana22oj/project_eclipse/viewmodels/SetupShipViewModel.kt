package se.ju.jana22oj.project_eclipse.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.garrit.android.multiplayer.SupabaseService
import kotlinx.coroutines.launch
import se.ju.jana22oj.project_eclipse.viewmodels.Board.BoardSize


class Ship(val type: ShipType, val coordinates: List<Coordinates>)
data class Coordinates(val x: Int, val y: Int)
enum class ShipType(val size: Int) {
    CARRIER(4),
    BATTLESHIP(3),
    CRUISER(2),
    DESTROYER(1)
}

object Board {
    const val BoardSize = 10
    private val _cells = mutableListOf<Cell>()

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


}

class Cell(val coordinates: Coordinates) {
    private var _occupant: Ship? = null

    fun occupy(ship: Ship) {
        _occupant = ship
    }

    fun clear() {
        _occupant = null
    }

    fun isOccupied(): Boolean {
        return _occupant != null
    }
}


class SetupShipViewModel: ViewModel() {
    private val _ships: MutableState<List<Ship>> = mutableStateOf(emptyList())
    val ships: List<Ship> = _ships.value


    val board = Board

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
        _ships.value = _ships.value + listOf(ship)

    }

    fun removeShip(ship: Ship) {
        // Remove the ship from the _ships list
        _ships.value = _ships.value - listOf(ship)

        // Remove the ship from the board (update board state)
        board.removeShip(ship)

    }

    private fun calculateShipCoordinates(shipType: ShipType, startCoordinate: Coordinates, isRotated: Boolean): List<Coordinates> {
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

    fun isValidPlacement(shipType: ShipType, coordinates: Coordinates, isRotated: Boolean): Boolean {
        val shipCoordinates = calculateShipCoordinates(shipType, coordinates, isRotated)

        // Check if all coordinates of the ship are within the board boundaries
        if (shipCoordinates.any { !isWithinBoardBoundaries(it) }) {
            return false
        }

        // Check if the ship can be placed without overlapping existing ships
        for (existingShip in ships) {
            if (shipCoordinates.any { existingShip.coordinates.contains(it) }) {
                return false
            }
        }

        return true
    }
}

