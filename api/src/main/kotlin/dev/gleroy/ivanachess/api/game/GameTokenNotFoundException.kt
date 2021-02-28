package dev.gleroy.ivanachess.api.game

import java.util.*

/**
 * Exception thrown when get non-existing game.
 *
 * @param token Player token.
 */
data class GameTokenNotFoundException(
    val token: UUID
) : RuntimeException() {
    override val message = "Game with token $token does not exist"
}
