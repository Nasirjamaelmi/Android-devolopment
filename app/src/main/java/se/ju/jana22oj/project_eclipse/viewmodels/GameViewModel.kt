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

data class  Coordinate(val row: Int, val column: Int)
data class Cell(
    val coordinate: Coordinate,
    val isClicked: MutableState<Boolean> = mutableStateOf(false),

)
data class Ship(
    val cells: List<Cell>,
    val isSunk: MutableState<Boolean> = mutableStateOf(false)
)

class GameViewModel: ViewModel() {

    private val _cells = mutableStateListOf<Cell>()

    val cells:SnapshotStateList<Cell>
        get() = _cells

    init {
        _cells.clear()
        val tempCards = mutableListOf<Cell>()
            for (i in 0..9)
            for (x in 0..9){
                tempCards.add(Cell(coordinate = Coordinate(i,x)))

            }
          _cells.addAll(tempCards)
        }


}

