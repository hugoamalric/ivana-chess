package dev.gleroy.ivanachess.api.user

/**
 * Exception thrown when get non-existing user.
 *
 * @param email Email.
 */
data class UserEmailNotFoundException(
    val email: String
) : RuntimeException() {
    override val message = "User with email '$email' does not exist"
}
