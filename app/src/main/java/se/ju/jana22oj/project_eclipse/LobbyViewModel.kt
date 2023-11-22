package se.ju.jana22oj.project_eclipse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.garrit.android.multiplayer.Game
import io.garrit.android.multiplayer.Player
import io.garrit.android.multiplayer.SupabaseService
import kotlinx.coroutines.launch

class LobbyViewModel : ViewModel() {

    //init {
    //    joinLobby(Player(name = "Hej"))
    //}

    val players = SupabaseService.users

    fun invitePlayer(opponent: Player) {
        viewModelScope.launch {
            SupabaseService.invite(opponent)
        }
    }

    fun acceptInvite(game: Game) {
        viewModelScope.launch {
            SupabaseService.acceptInvite(game)
        }
    }

    fun declineInvite(game: Game) {
        viewModelScope.launch {
            SupabaseService.declineInvite(game)
        }
    }

    fun joinLobby(player: Player) {
        viewModelScope.launch {
            SupabaseService.joinLobby(player)
        }
    }
}
