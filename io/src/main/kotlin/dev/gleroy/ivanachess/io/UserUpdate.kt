package dev.gleroy.ivanachess.io

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import javax.validation.constraints.Email
import javax.validation.constraints.Size

/**
 * User update.
 */
sealed class UserUpdate {
    /**
     * User update by admin.
     *
     * @param email Email.
     * @param password Password.
     * @param role Role.
     */
    data class Admin(
        @field:Email
        @JsonDeserialize(using = JacksonTrimStringDeserializer::class)
        override val email: String? = null,

        @field:Size(min = ApiConstants.Constraints.MinPasswordLength)
        override val password: String? = null,

        val role: UserRepresentation.Role? = null,
    ) : UserUpdate()

    /**
     * User update by user itself.
     *
     * @param email Email.
     * @param password Password.
     */
    data class Self(
        @field:Email
        @JsonDeserialize(using = JacksonTrimStringDeserializer::class)
        override val email: String? = null,

        @field:Size(min = ApiConstants.Constraints.MinPasswordLength)
        override val password: String? = null,
    ) : UserUpdate()

    /**
     * Email.
     */
    abstract val email: String?

    /**
     * Password.
     */
    abstract val password: String?
}
