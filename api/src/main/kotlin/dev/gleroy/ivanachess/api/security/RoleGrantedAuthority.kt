package dev.gleroy.ivanachess.api.security

import dev.gleroy.ivanachess.api.user.User
import org.springframework.security.core.GrantedAuthority

/**
 * Role implementation of granted authority.
 *
 * @param role Role.
 */
data class RoleGrantedAuthority(
    val role: User.Role
) : GrantedAuthority {
    override fun getAuthority() = when (role) {
        User.Role.Simple -> "simple"
        User.Role.Admin -> "admin"
        User.Role.SuperAdmin -> "super_admin"
    }
}
