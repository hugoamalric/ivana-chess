package dev.gleroy.ivanachess.io

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import javax.validation.constraints.Email
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/**
 * User subscription.
 *
 * @param pseudo Pseudo.
 * @param email Email.
 * @param password Password.
 */
data class UserSubscription(
    @JsonDeserialize(using = JacksonTrimStringDeserializer::class)
    @field:Size(min = ApiConstants.Constraints.MinPseudoLength, max = ApiConstants.Constraints.MaxPseudoLength)
    @field:Pattern(regexp = ApiConstants.Constraints.PseudoRegex)
    val pseudo: String,

    @JsonDeserialize(using = JacksonTrimStringDeserializer::class)
    @field:Email
    val email: String,

    @field:Size(min = ApiConstants.Constraints.MinPasswordLength)
    val password: String,
)
