package dev.gleroy.ivanachess.io

import java.util.*

/**
 * Representation of user on queue.
 *
 * @param id ID.
 * @param pseudo Pseudo.
 */
data class UserQueueMessage(
    val id: UUID,
    val pseudo: String,
)
