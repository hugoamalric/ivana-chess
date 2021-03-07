package dev.gleroy.ivanachess.api.game

import dev.gleroy.ivanachess.api.user.User
import java.util.*

/**
 * Exception thrown when an user tries to play in game in which it is not player.
 *
 * @param id Game ID.
 * @param user User.
 */
data class NotAllowedPlayerException(
    val id: UUID,
    val user: User
) : RuntimeException() {
    override val message = "User '${user.pseudo}' (${user.id}) tries to play in game $id"
}
