package dev.gleroy.ivanachess.io

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import java.util.*

/**
 * Representation of user.
 */
sealed class UserRepresentation : Representation {
    /**
     * Private representation of user.
     *
     * @param id ID.
     * @param pseudo Pseudo.
     * @param email Email.
     * @param creationDate Creation date.
     * @param role Role.
     */
    data class Private(
        override val id: UUID,
        override val pseudo: String,
        val email: String,
        override val creationDate: OffsetDateTime,
        override val role: Role,
    ) : UserRepresentation()

    /**
     * Public representation of user.
     *
     * @param id ID.
     * @param pseudo Pseudo.
     * @param creationDate Creation date.
     * @param role Role.
     */
    data class Public(
        override val id: UUID,
        override val pseudo: String,
        override val creationDate: OffsetDateTime,
        override val role: Role,
    ) : UserRepresentation()

    /**
     * Representation of user role.
     */
    enum class Role {
        /**
         * Simple.
         */
        @JsonProperty("simple")
        Simple,

        /**
         * Admin.
         */
        @JsonProperty("admin")
        Admin,

        /**
         * Super admin.
         */
        @JsonProperty("super_admin")
        SuperAdmin
    }

    /**
     * ID.
     */
    abstract val id: UUID

    /**
     * Pseudo.
     */
    abstract val pseudo: String

    /**
     * Creation date.
     */
    abstract val creationDate: OffsetDateTime

    /**
     * Role.
     */
    abstract val role: Role
}
