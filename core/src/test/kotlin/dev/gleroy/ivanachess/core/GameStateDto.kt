package dev.gleroy.ivanachess.core

import com.fasterxml.jackson.annotation.JsonProperty

internal enum class GameStateDto(
    val coreState: Game.State
) {
    @JsonProperty("in_game")
    InGame(Game.State.InGame),

    @JsonProperty("checkmate")
    Checkmate(Game.State.Checkmate),

    @JsonProperty("stalemate")
    Stalemate(Game.State.Stalemate);
}
