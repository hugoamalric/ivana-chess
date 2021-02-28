package dev.gleroy.ivanachess.api.security

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UserDetailsService

/**
 * Authentication service.
 */
interface AuthenticationService : UserDetailsService {
    /**
     * Generate JWT.
     *
     * @param pseudo User pseudo.
     * @param password User password.
     */
    @Throws(BadCredentialsException::class)
    fun generateJwt(pseudo: String, password: String): Jwt

    override fun loadUserByUsername(username: String): UserDetailsAdapter

    /**
     * Parse JWT value.
     *
     * @param token Token.
     * @return JWT.
     * @throws BadJwtException If JWT value is invalid.
     */
    @Throws(BadJwtException::class)
    fun parseJwt(token: String): Jwt
}
