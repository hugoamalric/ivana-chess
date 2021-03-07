package dev.gleroy.ivanachess.api.game

import java.util.*

/**
 * Exception thrown when get non-existing game.
 *
 * @param id Game ID.
 */
data class GameNotFoundException(
    val id: UUID
) : RuntimeException() {
    override val message = "Game $id does not exist"
}
