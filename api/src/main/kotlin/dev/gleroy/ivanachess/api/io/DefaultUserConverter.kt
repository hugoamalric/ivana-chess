package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.User
import dev.gleroy.ivanachess.io.UserConverter
import dev.gleroy.ivanachess.io.UserRepresentation
import org.springframework.stereotype.Component

/**
 * Default implementation of user converter.
 */
@Component
class DefaultUserConverter : UserConverter {
    override fun convertToRepresentation(item: User) = UserRepresentation(
        id = item.id,
        pseudo = item.pseudo,
        creationDate = item.creationDate,
        role = item.role.toRepresentation(),
    )

    /**
     * Convert user role to its representation.
     *
     * @return Representation of role.
     */
    private fun User.Role.toRepresentation() = when (this) {
        User.Role.Simple -> UserRepresentation.Role.Simple
        User.Role.Admin -> UserRepresentation.Role.Admin
        User.Role.SuperAdmin -> UserRepresentation.Role.SuperAdmin
    }
}
