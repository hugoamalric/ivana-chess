package dev.gleroy.ivanachess.io

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import java.util.*

/**
 * Representation of user.
 *
 * @param id ID.
 * @param pseudo Pseudo.
 * @param creationDate Creation date.
 * @param role Role.
 */
data class UserRepresentation(
    val id: UUID,
    val pseudo: String,
    val creationDate: OffsetDateTime,
    val role: Role = Role.Simple,
) {
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
}
