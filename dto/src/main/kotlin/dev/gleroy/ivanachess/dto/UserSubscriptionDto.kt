package dev.gleroy.ivanachess.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import javax.validation.constraints.Email
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/**
 * User subscription DTO.
 *
 * @param pseudo Pseudo.
 * @param email Email.
 * @param password Password.
 */
data class UserSubscriptionDto(
    @JsonDeserialize(using = JacksonTrimStringDeserializer::class)
    @field:Size(min = PseudoMinLength, max = PseudoMaxLength)
    @field:Pattern(regexp = PseudoRegex)
    val pseudo: String,

    @JsonDeserialize(using = JacksonTrimStringDeserializer::class)
    @field:Email
    val email: String,

    @JsonDeserialize(using = JacksonTrimStringDeserializer::class)
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
         * Pseudo validation regex.
         */
        const val PseudoRegex = "^[A-z0-9_-]+$"

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
