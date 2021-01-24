package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI
import java.util.*

/**
 * Game DTO.
 *
 * @param id ID.
 * @param whiteUrl URL to white player.
 * @param blackUrl URL to black player.
 * @param colorToPlay Color which must play next move.
 * @param state State.
 * @param pieces Set of pieces on board.
 * @param moves List of moves since the begin of the game.
 * @param possibleMoves Possible moves.
 */
data class GameDto(
    val id: UUID,
    val whiteUrl: URI,
    val blackUrl: URI,
    val colorToPlay: PieceDto.Color,
    val state: State,
    val pieces: Set<PieceDto>,
    val moves: List<MoveDto>,
    val possibleMoves: Set<MoveDto>
) {
    /**
     * State.
     */
    enum class State {
        /**
         * If players are playing.
         */
        @JsonProperty("in_game")
        InGame,

        /**
         * If game is ended by checkmate.
         */
        @JsonProperty("checkmate")
        Checkmate,

        /**
         * If game is ended by draw.
         */
        @JsonProperty("draw")
        Draw
    }
}
