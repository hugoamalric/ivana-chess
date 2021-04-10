package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.User

/**
 * Role SQL enumeration value.
 *
 * @param label SQL label.
 * @param role User role.
 */
internal enum class RoleSqlEnumValue(
    override val label: String,
    val role: User.Role,
) : SqlEnumValue {
    /**
     * Simple.
     */
    Simple("simple", User.Role.Simple),

    /**
     * Admin.
     */
    Admin("admin", User.Role.Admin),

    /**
     * Super admin.
     */
    SuperAdmin("super_admin", User.Role.SuperAdmin);

    companion object {
        /**
         * Get role SQL enumeration value from role.
         *
         * @param role Role.
         * @return Role SQL enumeration value.
         */
        fun from(role: User.Role) = when (role) {
            User.Role.Simple -> Simple
            User.Role.Admin -> Admin
            User.Role.SuperAdmin -> SuperAdmin
        }
    }
}
