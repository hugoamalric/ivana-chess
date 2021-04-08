package dev.gleroy.ivanachess.game

/**
 * Exception thrown when a user try to do invalid move.
 *
 * @param message Error message.
 */
data class InvalidMoveException(
    override val message: String
) : RuntimeException()
