package dev.gleroy.ivanachess.io

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import java.util.*

private const val InGameStateLabel = "in_game"
private const val StalemateStateLabel = "stalemate"
private const val CheckmateStateLabel = "checkmate"

/**
 * Representation of game.
 */
sealed class GameRepresentation : Representation {
    /**
     * Complete representation of game.
     *
     * @param id ID.
     * @param creationDate Creation date.
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
        override val creationDate: OffsetDateTime,
        override val whitePlayer: UserRepresentation.Public,
        override val blackPlayer: UserRepresentation.Public,
        override val turnColor: ColorRepresentation?,
        override val state: State,
        override val winnerColor: ColorRepresentation?,
        val pieces: Set<PieceRepresentation>,
        val moves: List<MoveRepresentation>,
        val possibleMoves: Set<MoveRepresentation>,
    ) : GameRepresentation()

    /**
     * Summary representation of game.
     *
     * @param id ID.
     * @param creationDate Creation date.
     * @param whitePlayer White player.
     * @param blackPlayer Black player.
     * @param turnColor Color for which is turn to play or null if game is over.
     * @param state State.
     * @param winnerColor Color of winner or null if the game is not checkmate.
     */
    data class Summary(
        override val id: UUID,
        override val creationDate: OffsetDateTime,
        override val whitePlayer: UserRepresentation.Public,
        override val blackPlayer: UserRepresentation.Public,
        override val turnColor: ColorRepresentation?,
        override val state: State,
        override val winnerColor: ColorRepresentation?,
    ) : GameRepresentation()

    /**
     * Representation of game state.
     *
     * @param label Label.
     */
    enum class State(
        val label: String,
    ) {
        /**
         * If players are playing.
         */
        @JsonProperty(InGameStateLabel)
        InGame(InGameStateLabel),

        /**
         * If game is ended by checkmate.
         */
        @JsonProperty(CheckmateStateLabel)
        Checkmate(CheckmateStateLabel),

        /**
         * If game is ended by stalemate.
         */
        @JsonProperty(StalemateStateLabel)
        Stalemate(StalemateStateLabel)
    }

    /**
     * ID.
     */
    abstract val id: UUID

    /**
     * Creation date.
     */
    abstract val creationDate: OffsetDateTime

    /**
     * White player.
     */
    abstract val whitePlayer: UserRepresentation.Public

    /**
     * Black player.
     */
    abstract val blackPlayer: UserRepresentation.Public

    /**
     * Color for which is turn to play or null if game is over.
     */
    abstract val turnColor: ColorRepresentation?

    /**
     * State.
     */
    abstract val state: State

    /**
     * Color of winner or null if the game is not checkmate.
     */
    abstract val winnerColor: ColorRepresentation?
}
