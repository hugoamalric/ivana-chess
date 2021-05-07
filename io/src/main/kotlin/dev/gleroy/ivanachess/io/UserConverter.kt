package dev.gleroy.ivanachess.io

import dev.gleroy.ivanachess.core.User

/**
 * User converter.
 */
interface UserConverter {
    /**
     * Convert user to its public representation.
     *
     * @param user User.
     * @return Representation of user.
     */
    fun convertToPublicRepresentation(user: User): UserRepresentation.Public
}
