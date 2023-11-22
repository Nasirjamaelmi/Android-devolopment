package se.ju.jana22oj.project_eclipse


import io.garrit.android.multiplayer.Game
import io.garrit.android.multiplayer.Player
import io.garrit.android.multiplayer.SupabaseService
import kotlinx.coroutines.launch

class LobbyRepository() {

    fun refreshLobbyData(): Pair<List<Player>, List<Game>> {
        val players = SupabaseService.users
        val games = SupabaseService.games
        return Pair(players, games)
    }

    suspend fun invitePlayer(opponent: Player) {
        SupabaseService.invite(opponent)
    }

    suspend fun acceptInvite(game: Game) {
        SupabaseService.acceptInvite(game)
    }

    suspend fun declineInvite(game: Game) {
        SupabaseService.declineInvite(game)
    }

    suspend fun joinLobby(player: Player) {
        SupabaseService.joinLobby(player)
    }
}
