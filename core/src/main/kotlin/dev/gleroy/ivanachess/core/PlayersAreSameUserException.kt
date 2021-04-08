package dev.gleroy.ivanachess.core

/**
 * Exception thrown when try to create new game with same user as white and black player.
 */
class PlayersAreSameUserException : RuntimeException() {
    override val message = "Same user as white and black player"
}
