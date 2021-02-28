package dev.gleroy.ivanachess.api.game

import dev.gleroy.ivanachess.core.InvalidMoveException
import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.core.Piece
import java.util.*

/**
 * Exception thrown when an error occurs when a player tries to play.
 */
sealed class PlayException : RuntimeException() {
    /**
     * Exception thrown when a player tries to play invalid move.
     *
     * @param id Game ID.
     * @param token Token.
     * @param color Color of player.
     * @param move Move.
     * @param cause Cause of error.
     */
    class InvalidMove(
        val id: UUID,
        val token: UUID,
        val color: Piece.Color,
        val move: Move,
        override val cause: InvalidMoveException
    ) : PlayException() {
        override val message = "Player $token ($color) tries to play invalid move $move in game $id"
    }

    /**
     * Exception thrown when a player tries to steal turn.
     *
     * @param id Game ID.
     * @param token Token.
     * @param color Color of player which tries to steal turn.
     */
    data class InvalidPlayer(
        val id: UUID,
        val token: UUID,
        val color: Piece.Color
    ) : PlayException() {
        override val message = "Player $token ($color) tries to steal turn in game $id"
    }

    abstract override val message: String
}
