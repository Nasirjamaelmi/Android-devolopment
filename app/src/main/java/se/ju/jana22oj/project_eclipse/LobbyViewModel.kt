package se.ju.jana22oj.project_eclipse

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.garrit.android.multiplayer.Game
import io.garrit.android.multiplayer.Player
import io.garrit.android.multiplayer.ServerState
import io.garrit.android.multiplayer.SupabaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LobbyViewModel : ViewModel() {


     val players = SupabaseService.users
     val server : MutableStateFlow<ServerState> = SupabaseService.serverState

     val games = SupabaseService.games
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
