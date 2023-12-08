package se.ju.jana22oj.project_eclipse.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import se.ju.jana22oj.project_eclipse.R
import se.ju.jana22oj.project_eclipse.viewmodels.Board
import se.ju.jana22oj.project_eclipse.viewmodels.Coordinates
import se.ju.jana22oj.project_eclipse.viewmodels.SetupShipViewModel
import se.ju.jana22oj.project_eclipse.viewmodels.Ship
import se.ju.jana22oj.project_eclipse.viewmodels.ShipType



@Composable
fun SetupShipScreen(setupShipViewModel: SetupShipViewModel = viewModel(),navController: NavController) {
    val ships = setupShipViewModel.ships
    val availableShipTypes = setupShipViewModel.availabeshipTypes
    val selectedShipType: State<ShipType> = remember(availableShipTypes) {
        derivedStateOf {
            availableShipTypes.firstOrNull() ?: ShipType.CARRIER
        }
    }
    val isRotated = remember { mutableStateOf(false) } // to handle ship rotation


    Box(modifier = Modifier.fillMaxSize()){
        Image(painter = painterResource(id = R.drawable.my_setup),
            contentDescription = "BackgroundImage",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize())
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "SETUP YOUR SHIPS",
            fontWeight = FontWeight.Bold,
            color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        if (availableShipTypes.isNotEmpty()) {
            // Dropdown menu for ship selection
            DropdownMenuWithIcons(selectedShipType, availableShipTypes)

            // Button to toggle ship rotation
            Button(onClick = { isRotated.value = !isRotated.value }) {
                Text(text = if (isRotated.value) "Horizontal" else "Vertical")
            }

        }
        // Grid for ship placement
        LazyVerticalGrid(
            columns = GridCells.Fixed(Board.BoardSize),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(Board.BoardSize * Board.BoardSize) { index ->
                val x = index % Board.BoardSize
                val y = index / Board.BoardSize
                CellView(
                    Coordinates(x, y),
                    ships,
                    setupShipViewModel,
                    selectedShipType.value,
                    isRotated.value
                )
            }
        }
        if (setupShipViewModel.isSetupComplete) {
            Text("Setup is completed",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            // READY button
            Button(onClick = { setupShipViewModel.startGame()
                navController.navigate(route = Screen.Game.route)}) {
                Text("READY")
            }
        }
    }
}

@Composable
fun DropdownMenuWithIcons(selectedShipType: State<ShipType>, availableShipTypes: List<ShipType>) {
    var expanded by remember { mutableStateOf(false) }


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
            availableShipTypes.forEach { shipType ->
                DropdownMenuItem(text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ShipIcon(shipType, Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(shipType.name)
                    }
                }, onClick = {
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
            if (!viewModel.isSetupComplete) {
                if (shipInCell == null) {
                    viewModel.placeShip(shipType, coordinates, isRotated)
                }
            }
        },
        modifier = Modifier
            .aspectRatio(1f) // Ensure the cell is square
            .border(1.dp, Color.Black) // Adds a border
            .background(if (shipInCell != null) Color.Transparent else Color.Gray)
            .padding(4.dp), // Add padding to ensure icons fit within the cell
        colors = ButtonDefaults.buttonColors(containerColor = if (shipInCell != null) Color.Transparent else Color.Gray),
        shape = RectangleShape,
        contentPadding = PaddingValues(0.dp),
        content = {
            Box(contentAlignment = Alignment.Center) {
                shipInCell?.let {
                    ShipIcon(it.type, Modifier.fillMaxSize())
                }
            }
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
