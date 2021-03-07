package dev.gleroy.ivanachess.api.game

import java.util.*

/**
 * Exception thrown when user tries to create game with player which does not exist.
 *
 * @param id User ID.
 */
data class PlayerNotFoundException(
    val id: UUID
) : RuntimeException() {
    override val message = "User $id does not exist"
}
