package dev.gleroy.ivanachess.io

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 * Representation of game.
 */
sealed class GameRepresentation {
    /**
     * Complete representation of game.
     *
     * @param id ID.
     * @param whitePlayer White player.
     * @param blackPlayer Black player.
     * @param turnColor Color for which is turn to play or null if game is over.
     * @param state State.
     * @param winnerColor Color of winner or null if the game is not checkmate.
     * @param pieces Set of pieces on board.
     * @param moves List of moves since the begin of the game.
     * @param possibleMoves Possible moves.
     */
    data class Complete(
        override val id: UUID,
        override val whitePlayer: UserRepresentation,
        override val blackPlayer: UserRepresentation,
        override val turnColor: PieceRepresentation.Color?,
        override val state: State,
        override val winnerColor: PieceRepresentation.Color?,
        val pieces: Set<PieceRepresentation>,
        val moves: List<MoveRepresentation>,
        val possibleMoves: Set<MoveRepresentation>,
    ) : GameRepresentation()

    /**
     * Summary representation of game.
     *
     * @param id ID.
     * @param whitePlayer White player.
     * @param blackPlayer Black player.
     * @param turnColor Color for which is turn to play or null if game is over.
     * @param state State.
     * @param winnerColor Color of winner or null if the game is not checkmate.
     */
    data class Summary(
        override val id: UUID,
        override val whitePlayer: UserRepresentation,
        override val blackPlayer: UserRepresentation,
        override val turnColor: PieceRepresentation.Color?,
        override val state: State,
        override val winnerColor: PieceRepresentation.Color?,
    ) : GameRepresentation()

    /**
     * Representation of game state.
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
    abstract val whitePlayer: UserRepresentation

    /**
     * Black player.
     */
    abstract val blackPlayer: UserRepresentation

    /**
     * Color for which is turn to play or null if game is over.
     */
    abstract val turnColor: PieceRepresentation.Color?

    /**
     * State.
     */
    abstract val state: State

    /**
     * Color of winner or null if the game is not checkmate.
     */
    abstract val winnerColor: PieceRepresentation.Color?
}
