package dev.gleroy.ivanachess.api.security

import dev.gleroy.ivanachess.api.User
import org.springframework.security.core.userdetails.UserDetails

/**
 * User details implementation based on user entity.
 *
 * @param user User.
 */
data class UserDetailsAdapter(
    val user: User
) : UserDetails {
    override fun getAuthorities() = setOf(RoleGrantedAuthority(user.role))

    override fun isEnabled() = true

    override fun getUsername() = user.pseudo

    override fun isCredentialsNonExpired() = true

    override fun getPassword() = user.bcryptPassword

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true
}
