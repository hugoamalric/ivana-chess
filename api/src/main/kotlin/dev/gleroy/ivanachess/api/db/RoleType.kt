package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.user.User

/**
 * Role type.
 *
 * @param sqlValue SQL type value.
 * @param role Role.
 */
internal enum class RoleType(
    val sqlValue: String,
    val role: User.Role
) {
    Simple("simple", User.Role.Simple),
    Admin("admin", User.Role.Admin),
    SuperAdmin("super_admin", User.Role.SuperAdmin);

    companion object {
        /**
         * Get role type from role.
         *
         * @param role Role.
         * @return Role type.
         */
        fun from(role: User.Role) = when (role) {
            User.Role.Simple -> Simple
            User.Role.Admin -> Admin
            User.Role.SuperAdmin -> SuperAdmin
        }

        /**
         * Get role type from SQL type value.
         *
         * @param sqlValue SQL type value.
         * @return Role type.
         * @throws IllegalArgumentException If SQL type value is not a valid role.
         */
        @Throws(IllegalArgumentException::class)
        fun from(sqlValue: String) = values().find { it.sqlValue == sqlValue }
            ?: throw IllegalArgumentException("Unknown role '$sqlValue'")
    }
}
