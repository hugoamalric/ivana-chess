package dev.gleroy.ivanachess.api.security

import org.springframework.security.core.Authentication

/**
 * Authentication implementation based on user details.
 *
 * @param userDetails User details.
 */
data class AuthenticationAdapter(
    val userDetails: UserDetailsAdapter
) : Authentication {
    override fun getAuthorities() = principal.authorities

    override fun setAuthenticated(isAuthenticated: Boolean) {

    }

    override fun getName() = principal.username

    override fun getCredentials() = null

    override fun getPrincipal() = userDetails

    override fun isAuthenticated() = true

    override fun getDetails() = principal
}
