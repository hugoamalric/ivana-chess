package dev.gleroy.ivanachess.core

/**
 * Exception thrown when try to create user with an already used pseudo.
 *
 * @param pseudo Pseudo.
 */
data class UserPseudoAlreadyUsedException(
    val pseudo: String
) : RuntimeException() {
    override val message = "User '$pseudo' already exists"
}
