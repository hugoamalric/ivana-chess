package dev.gleroy.ivanachess.api.security

import java.time.OffsetDateTime

/**
 * JWT.
 *
 * @param pseudo User pseudo.
 * @param token Token.
 * @param expirationDate Expiration date.
 */
data class Jwt(
    val pseudo: String,
    val expirationDate: OffsetDateTime,
    val token: String
)
