package dev.gleroy.ivanachess.dto

import com.fasterxml.jackson.annotation.JsonProperty
import dev.gleroy.ivanachess.core.Game
import java.util.*

/**
 * Game DTO.
 *
 * @param id ID.
 * @param whiteToken Token to play as white player.
 * @param blackToken Token to play as black player.
 * @param turnColor Color for which it is turn to play.
 * @param state State.
 * @param pieces Set of pieces on board.
 * @param moves List of moves since the begin of the game.
 * @param possibleMoves Possible moves.
 */
data class GameDto(
    val id: UUID,
    val whiteToken: UUID,
    val blackToken: UUID,
    val turnColor: PieceDto.Color,
    val state: State,
    val pieces: Set<PieceDto>,
    val moves: List<MoveDto>,
    val possibleMoves: Set<MoveDto>
) {
    /**
     * State.
     *
     * @param coreState State.
     */
    enum class State(
        val coreState: Game.State
    ) {
        /**
         * If players are playing.
         */
        @JsonProperty("in_game")
        InGame(Game.State.InGame),

        /**
         * If game is ended by checkmate.
         */
        @JsonProperty("checkmate")
        Checkmate(Game.State.Checkmate),

        /**
         * If game is ended by stalemate.
         */
        @JsonProperty("stalemate")
        Stalemate(Game.State.Stalemate)
    }
}
