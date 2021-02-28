package dev.gleroy.ivanachess.api.security

/**
 * Exception thrown when an error occurs during JWT value parsing.
 *
 * @param message Error message.
 * @param cause Cause of error.
 */
class BadJwtException(
    override val message: String,
    override val cause: Throwable? = null
) : RuntimeException()
