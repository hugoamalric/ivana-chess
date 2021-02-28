package dev.gleroy.ivanachess.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import java.util.*

/**
 * User DTO.
 *
 * @param id ID.
 * @param pseudo Pseudo.
 * @param creationDate Creation date.
 * @param role Role.
 */
data class UserDto(
    val id: UUID,
    val pseudo: String,
    val creationDate: OffsetDateTime,
    val role: Role = Role.Simple
) {
    /**
     * Role.
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
