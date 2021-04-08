package dev.gleroy.ivanachess.core

import java.util.*

/**
 * Exception thrown when a player tries to steal turn of other player.
 *
 * @param id Game ID.
 * @param player Player.
 */
data class InvalidPlayerException(
    val id: UUID,
    val player: User
) : RuntimeException() {
    override val message = "Player '${player.pseudo}' (${player.id}) tries to steal turn in game $id"
}
