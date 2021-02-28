package dev.gleroy.ivanachess.api

/**
 * Exception thrown when get non-existing user.
 *
 * @param pseudo Pseudo..
 */
data class UserPseudoNotFoundException(
    val pseudo: String
) : RuntimeException() {
    override val message = "User '$pseudo' does not exist"
}
