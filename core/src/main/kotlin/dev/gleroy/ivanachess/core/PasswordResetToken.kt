package dev.gleroy.ivanachess.core

import java.time.OffsetDateTime
import java.util.*

/**
 * Password reset token.
 *
 * @param id ID.
 * @param creationDate Creation date.
 * @param userId User ID.
 * @param expirationDate Expiration date.
 */
data class PasswordResetToken(
    override val id: UUID = UUID.randomUUID(),
    override val creationDate: OffsetDateTime = OffsetDateTime.now(),
    val userId: UUID,
    val expirationDate: OffsetDateTime,
) : Entity
