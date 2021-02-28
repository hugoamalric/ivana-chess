package dev.gleroy.ivanachess.api

import java.util.*

/**
 * Exception thrown when get non-existing user.
 *
 * @param id User ID.
 */
data class UserIdNotFoundException(
    val id: UUID
) : RuntimeException() {
    override val message = "User $id does not exist"
}
