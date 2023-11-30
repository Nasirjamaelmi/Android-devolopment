package se.ju.jana22oj.project_eclipse.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import java.util.Timer
import kotlin.concurrent.schedule
/*
    data class  Coordinate(val row: Int, val column: Int)
    data class sCell(
        val coordinate: Coordinate,
        val isClicked: MutableState<Boolean> = mutableStateOf(false),
        val color: MutableState<Color> = mutableStateOf(Color.Black)

    )
    data class sShip(
        val cells: List<Cell>,
        val isSunk: MutableState<Boolean> = mutableStateOf(false)
    )

    class GameViewModel: ViewModel() {

        private val _cells = mutableStateListOf<Cell>()
        private val _ships = mutableStateListOf<Ship>()


        val cells:SnapshotStateList<Cell>
            get() = _cells

        val ships:SnapshotStateList<Ship>
            get() = _ships

        init {
            _cells.clear()
            val tempCards = mutableListOf<Cell>()
                for (i in 0..9)
                    for (x in 0..9){
                    tempCards.add(Cell(coordinate = Coordinate(i,x)))
                }
              _cells.addAll(tempCards)
            }
        fun selectCell(cell:Cell){
            if(_ships.any {it.cells.contains(cell)}) {
                return
            }
            if (cell.isClicked.value){
                cell.isClicked.value = false
            }
            else{
                cell.isClicked.value = true
                checkAndCreateShip()
            }
        }
        private fun checkAndCreateShip() {
            val selectedCells = _cells.filter { it.isClicked.value }
            if (selectedCells.size == 4) {
                val newShip = Ship(cells = selectedCells)
                _ships.add(newShip)

                // Change the color of selected cells to blue
                val blueColor = Color.Blue
                selectedCells.forEach {
                    it.isClicked.value = false
                    it.color.value = blueColor
                } // Deselect cells
                selectedCells.forEach { it.coordinate } // Change color or update some property
                // Notify observers that a ship has been created
                // You might want to use LiveData or other mechanisms to observe changes
            }
        }
    }
*/