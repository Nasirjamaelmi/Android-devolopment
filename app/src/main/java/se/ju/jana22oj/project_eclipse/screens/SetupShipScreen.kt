package se.ju.jana22oj.project_eclipse.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import se.ju.jana22oj.project_eclipse.R
import se.ju.jana22oj.project_eclipse.viewmodels.Board
import se.ju.jana22oj.project_eclipse.viewmodels.Coordinates
import se.ju.jana22oj.project_eclipse.viewmodels.SetupShipViewModel
import se.ju.jana22oj.project_eclipse.viewmodels.Ship
import se.ju.jana22oj.project_eclipse.viewmodels.ShipType


@Composable
fun SetupShipScreen(setupShipViewModel: SetupShipViewModel = viewModel()) {
    val ships = setupShipViewModel.ships
    val selectedShipType = remember { mutableStateOf(ShipType.DESTROYER) } // default selection
    val isRotated = remember { mutableStateOf(false) } // to handle ship rotation

    Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "SETUP YOUR SHIPS")
        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown menu for ship selection
        DropdownMenuWithIcons(selectedShipType)

        // Button to toggle ship rotation
        Button(onClick = { isRotated.value = !isRotated.value }) {
            Text(text = if (isRotated.value) "Horizontal" else "Vertical")
        }

        // Grid for ship placement
        LazyVerticalGrid(
                columns = GridCells.Fixed(Board.BoardSize),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(Board.BoardSize * Board.BoardSize) { index ->
                val x = index % Board.BoardSize
                val y = index / Board.BoardSize
                CellView(Coordinates(x, y), ships, setupShipViewModel, selectedShipType.value, isRotated.value)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ready button
        Button(onClick = { setupShipViewModel.startGame() }) {
            Text("READY")
        }
    }
}

@Composable
fun DropdownMenuWithIcons(selectedShipType: MutableState<ShipType>) {
    var expanded by remember { mutableStateOf(false) }
    val shipTypes = ShipType.values()

    Box {
        Row(modifier = Modifier
            .clickable { expanded = true }
            .padding(8.dp)) {
            ShipIcon(selectedShipType.value, Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            Text(text = selectedShipType.value.name)
        }
        DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
        ) {
            shipTypes.forEach { shipType ->
                DropdownMenuItem(text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ShipIcon(shipType, Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(shipType.name)
                    }
                }, onClick = {
                    selectedShipType.value = shipType
                    expanded = false
                })
            }
        }
    }
}




@Composable
fun CellView(coordinates: Coordinates, ships: List<Ship>, viewModel: SetupShipViewModel, shipType: ShipType, isRotated: Boolean) {
    val shipInCell = ships.find { ship -> coordinates in ship.coordinates }
    Button(
            onClick = {
                if (shipInCell == null) viewModel.placeShip(shipType, coordinates, isRotated)
            },
            modifier = Modifier
                .aspectRatio(1f)
                .background(if (shipInCell != null) Color.Red else Color.Gray),
            content = {
                shipInCell?.let { ShipIcon(it.type) }
            }
    )
}



@Composable
fun ShipIcon(shipType: ShipType, modifier: Modifier = Modifier) {
    val iconResource = when (shipType) {
        ShipType.CARRIER -> R.drawable.icon_carrier
        ShipType.BATTLESHIP -> R.drawable.icon_battleship
        ShipType.CRUISER -> R.drawable.icon_crusier
        ShipType.DESTROYER -> R.drawable.icon_destroyer
    }

    Image(
            painter = painterResource(id = iconResource),
            contentDescription = "Ship Icon",
            modifier = modifier
    )
}


@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun DefaultPreview(){
    SetupShipScreen()
}