package dev.gleroy.ivanachess.dto

import javax.validation.constraints.Size

/**
 * User subscription DTO.
 *
 * @param pseudo Pseudo.
 * @param password Password.
 */
data class UserSubscriptionDto(
    @field:Size(min = PseudoMinLength, max = PseudoMaxLength)
    val pseudo: String,

    @field:Size(min = PasswordMinLength)
    val password: String
) {
    companion object {
        /**
         * Pseudo minimal length.
         */
        const val PseudoMinLength = 3

        /**
         * Pseudo maximal length.
         */
        const val PseudoMaxLength = 50

        /**
         * Password minimal length.
         */
        const val PasswordMinLength = 5

        /**
         * Password maximal length.
         */
        const val PasswordMaxLength = Int.MAX_VALUE
    }
}
