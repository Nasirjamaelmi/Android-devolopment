package se.ju.jana22oj.project_eclipse

import android.os.Bundle
import android.widget.GridLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import se.ju.jana22oj.project_eclipse.ui.theme.ProjecteclipseTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class Cell(
    val row: Int,
    val col:Int,
    var isShip: Boolean = false,
    var isHit: Boolean = false )

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjecteclipseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
                    //GameBoard()
                }
            }
        }
    }
}

@Composable
fun GameBoard(){
    Row {
        for(y in 0..9)
            Column {
                for(x in 0..9)
                {
                    Box()
                }
            }
    }
}
@Composable
fun Box(){
    Text(text = "|_|")
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProjecteclipseTheme {
        Greeting("Android")
    }
}