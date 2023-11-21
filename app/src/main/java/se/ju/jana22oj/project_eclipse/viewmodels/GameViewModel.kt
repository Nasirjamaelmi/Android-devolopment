package se.ju.jana22oj.project_eclipse.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.AddCircle
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import java.util.Timer
import kotlin.concurrent.schedule

data class Card(val symbol: ImageVector,
    val isFlipped: MutableState<Boolean> = mutableStateOf(false),
    val isActive: MutableState<Boolean> = mutableStateOf(true)

){
    fun  flip()
    {
        isFlipped.value = true

    }
    fun reset()
    {
        isFlipped.value = false
    }
    fun deactivate()
    {
        isActive.value = false
    }
}

class GameViewModel: ViewModel() {
    private val _symbols = setOf(
        Icons.Rounded.Warning,
        Icons.Rounded.AccountBox,
        Icons.Rounded.Build,
        Icons.Rounded.ArrowForward,
        Icons.Rounded.AddCircle,
        Icons.Rounded.CheckCircle,
        Icons.Rounded.DateRange,
        Icons.Rounded.Email
    )
    private val _cards = mutableStateListOf<Card>()
    val cards:SnapshotStateList<Card>
        get() = _cards

    init {
        _cards.clear()
        val tempCards = mutableListOf<Card>()
        for (symbol in _symbols){
            tempCards.add(Card(symbol))
            tempCards.add(Card(symbol))
        }
        tempCards.shuffle()
        _cards.addAll(tempCards)
    }

    fun flipCard(card:Card){
        if (card.isActive.value &&  !card.isFlipped.value){
            card.flip()

            val openCards = _cards.filter { it.isFlipped.value && it.isActive.value}
            if (openCards.count() == 2) {
                if(openCards[0].symbol == openCards[1].symbol)
                {
                    println("Yay! You found a pair!")
                    openCards.forEach{
                        it.deactivate()
                    }
                }
                else{
                    Timer().schedule(1000){
                        openCards.forEach{
                            it.reset()
                        }
                    }
                }
            }
        }
    }
}