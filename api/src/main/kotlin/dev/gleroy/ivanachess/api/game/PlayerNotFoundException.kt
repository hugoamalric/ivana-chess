package dev.gleroy.ivanachess.api.game

import dev.gleroy.ivanachess.api.EntityNotFoundException

/**
 * Exception thrown when user tries to create game with player which does not exist.
 */
sealed class PlayerNotFoundException : RuntimeException() {
    /**
     * Exception thrown when white player does not exist.
     *
     * @param cause Cause of error.
     */
    class White(
        override val cause: EntityNotFoundException,
    ) : PlayerNotFoundException() {
        override val message get() = "White player does not exist"
    }

    /**
     * Exception thrown when black player does not exist.
     *
     * @param cause Cause of error.
     */
    class Black(
        override val cause: EntityNotFoundException,
    ) : PlayerNotFoundException() {
        override val message get() = "Black player does not exist"
    }

    abstract override val message: String

    abstract override val cause: EntityNotFoundException
}
