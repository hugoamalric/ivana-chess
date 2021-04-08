package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.User
import dev.gleroy.ivanachess.io.UserRepresentation

/**
 * User converter.
 */
interface UserConverter {
    /**
     * Convert user to its representation.
     *
     * @param user User.
     * @return Representation of user.
     */
    fun convertToRepresentation(user: User): UserRepresentation
}
