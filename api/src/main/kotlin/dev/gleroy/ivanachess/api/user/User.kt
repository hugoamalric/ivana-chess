package dev.gleroy.ivanachess.api.user

import dev.gleroy.ivanachess.api.Entity
import java.time.OffsetDateTime
import java.util.*

/**
 * User.
 *
 * @param id ID.
 * @param pseudo Pseudo.
 * @param creationDate Creation date.
 * @param bcryptPassword BCrypt hash of password.
 * @param role Role.
 */
data class User(
    override val id: UUID = UUID.randomUUID(),
    val pseudo: String,
    override val creationDate: OffsetDateTime = OffsetDateTime.now(),
    val bcryptPassword: String,
    val role: Role = Role.Simple
) : Entity {
    /**
     * Role.
     */
    enum class Role {
        /**
         * Simple.
         */
        Simple,

        /**
         * Admin.
         */
        Admin,

        /**
         * Super admin.
         */
        SuperAdmin
    }
}
