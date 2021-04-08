package dev.gleroy.ivanachess.core

/**
 * Exception thrown when try to create user with an already used email.
 *
 * @param email Email.
 */
data class UserEmailAlreadyUsedException(
    val email: String
) : RuntimeException() {
    override val message = "Email '$email' already used"
}
