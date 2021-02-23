package dev.gleroy.ivanachess.dto

import com.fasterxml.jackson.annotation.JsonProperty
import dev.gleroy.ivanachess.core.Game
import java.util.*

/**
 * Game DTO.
 */
sealed class GameDto {
    /**
     * Complete game DTO.
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
    data class Complete(
        override val id: UUID,
        override val whiteToken: UUID,
        override val blackToken: UUID,
        override val turnColor: PieceDto.Color,
        override val state: State,
        val pieces: Set<PieceDto>,
        val moves: List<MoveDto>,
        val possibleMoves: Set<MoveDto>
    ) : GameDto()

    /**
     * Summary game DTO.
     *
     * @param id ID.
     * @param whiteToken Token to play as white player.
     * @param blackToken Token to play as black player.
     * @param turnColor Color for which it is turn to play.
     * @param state State.
     */
    data class Summary(
        override val id: UUID,
        override val whiteToken: UUID,
        override val blackToken: UUID,
        override val turnColor: PieceDto.Color,
        override val state: State
    ) : GameDto()

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
        Stalemate(Game.State.Stalemate);

        companion object {
            /**
             * Get DTO from state.
             *
             * @param state Game state.
             * @return DTO.
             */
            fun from(state: Game.State) = when (state) {
                Game.State.InGame -> InGame
                Game.State.Checkmate -> Checkmate
                Game.State.Stalemate -> Stalemate
            }
        }
    }

    /**
     * ID.
     */
    abstract val id: UUID

    /**
     * White token.
     */
    abstract val whiteToken: UUID

    /**
     * Black token.
     */
    abstract val blackToken: UUID

    /**
     * Color for which is turn to play.
     */
    abstract val turnColor: PieceDto.Color

    /**
     * State.
     */
    abstract val state: State
}
