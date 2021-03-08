package dev.gleroy.ivanachess.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 * Game DTO.
 */
sealed class GameDto {
    /**
     * Complete game DTO.
     *
     * @param id ID.
     * @param whitePlayer White player.
     * @param blackPlayer Black player.
     * @param turnColor Color for which it is turn to play.
     * @param state State.
     * @param pieces Set of pieces on board.
     * @param moves List of moves since the begin of the game.
     * @param possibleMoves Possible moves.
     */
    data class Complete(
        override val id: UUID,
        override val whitePlayer: UserDto,
        override val blackPlayer: UserDto,
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
     * @param whitePlayer White player.
     * @param blackPlayer Black player.
     * @param turnColor Color for which it is turn to play.
     * @param state State.
     */
    data class Summary(
        override val id: UUID,
        override val whitePlayer: UserDto,
        override val blackPlayer: UserDto,
        override val turnColor: PieceDto.Color,
        override val state: State
    ) : GameDto()

    /**
     * State.
     *
     * @param coreState State.
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
         * If game is ended by stalemate.
         */
        @JsonProperty("stalemate")
        Stalemate
    }

    /**
     * ID.
     */
    abstract val id: UUID

    /**
     * White player.
     */
    abstract val whitePlayer: UserDto

    /**
     * Black player.
     */
    abstract val blackPlayer: UserDto

    /**
     * Color for which is turn to play.
     */
    abstract val turnColor: PieceDto.Color

    /**
     * State.
     */
    abstract val state: State
}
