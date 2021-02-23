package dev.gleroy.ivanachess.api

import java.util.*

/**
 * Exception thrown when a player tries to get non-existing game.
 *
 * @param id Game ID.
 */
data class GameIdNotFoundException(
    val id: UUID
) : RuntimeException() {
    override val message = "Game $id does not exist"
}
