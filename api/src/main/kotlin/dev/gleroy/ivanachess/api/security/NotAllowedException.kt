package dev.gleroy.ivanachess.api.security

/**
 * Exception thrown when user does forbidden operation.
 *
 * @param message Error message.
 */
class NotAllowedException(
    override val message: String,
) : RuntimeException()
