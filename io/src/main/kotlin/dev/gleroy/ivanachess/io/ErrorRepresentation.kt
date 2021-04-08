package dev.gleroy.ivanachess.io

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

private const val BadCredentialsCode = "bad_credentials"
private const val ForbiddenCode = "forbidden"
private const val EntityNotFoundCode = "entity_not_found"
private const val InvalidContentTypeCode = "invalid_content_type"
private const val InvalidMoveCode = "invalid_move"
private const val InvalidParameterCode = "invalid_parameter"
private const val InvalidPlayerCode = "invalid_player"
private const val InvalidRequestBodyCode = "invalid_request_body"
private const val MethodNotAllowedCode = "method_not_allowed"
private const val NotFoundCode = "not_found"
private const val PlayerNotFoundCode = "player_not_found"
private const val PlayersAreSameUserCode = "players_are_same_user"
private const val UnauthorizedCode = "unauthorized"
private const val UnexpectedErrorCode = "unexpected_error"
private const val UnsupportedFieldCode = "unsupported_field"
private const val UserEmailAlreadyUsedCode = "email_already_used"
private const val UserPseudoAlreadyUsedCode = "pseudo_already_used"
private const val ValidationErrorCode = "validation_error"

/**
 * Representation of error.
 */
@JsonTypeInfo(
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    use = JsonTypeInfo.Id.NAME,
    property = "code"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ErrorRepresentation.BadCredentials::class, name = BadCredentialsCode),
    JsonSubTypes.Type(value = ErrorRepresentation.EntityNotFound::class, name = EntityNotFoundCode),
    JsonSubTypes.Type(value = ErrorRepresentation.Forbidden::class, name = ForbiddenCode),
    JsonSubTypes.Type(value = ErrorRepresentation.InvalidContentType::class, name = InvalidContentTypeCode),
    JsonSubTypes.Type(value = ErrorRepresentation.InvalidMove::class, name = InvalidMoveCode),
    JsonSubTypes.Type(value = ErrorRepresentation.InvalidParameter::class, name = InvalidParameterCode),
    JsonSubTypes.Type(value = ErrorRepresentation.InvalidPlayer::class, name = InvalidPlayerCode),
    JsonSubTypes.Type(value = ErrorRepresentation.InvalidRequestBody::class, name = InvalidRequestBodyCode),
    JsonSubTypes.Type(value = ErrorRepresentation.MethodNotAllowed::class, name = MethodNotAllowedCode),
    JsonSubTypes.Type(value = ErrorRepresentation.NotFound::class, name = NotFoundCode),
    JsonSubTypes.Type(value = ErrorRepresentation.PlayerNotFound::class, name = PlayerNotFoundCode),
    JsonSubTypes.Type(value = ErrorRepresentation.PlayersAreSameUser::class, name = PlayersAreSameUserCode),
    JsonSubTypes.Type(value = ErrorRepresentation.Unauthorized::class, name = UnauthorizedCode),
    JsonSubTypes.Type(value = ErrorRepresentation.Unexpected::class, name = UnexpectedErrorCode),
    JsonSubTypes.Type(value = ErrorRepresentation.UnsupportedField::class, name = UnsupportedFieldCode),
    JsonSubTypes.Type(value = ErrorRepresentation.UserEmailAlreadyUsed::class, name = UserEmailAlreadyUsedCode),
    JsonSubTypes.Type(value = ErrorRepresentation.UserPseudoAlreadyUsed::class, name = UserPseudoAlreadyUsedCode),
    JsonSubTypes.Type(value = ErrorRepresentation.Validation::class, name = ValidationErrorCode),
)
sealed class ErrorRepresentation {
    /**
     * Representation of bad_credentials error.
     */
    object BadCredentials : ErrorRepresentation() {
        override val code get() = BadCredentialsCode

        override fun equals(other: Any?) = other is BadCredentials
    }

    /**
     * Representation of entity_not_found error.
     */
    object EntityNotFound : ErrorRepresentation() {
        override val code get() = EntityNotFoundCode

        override fun equals(other: Any?) = other is EntityNotFound
    }

    /**
     * Representation of forbidden error.
     */
    object Forbidden : ErrorRepresentation() {
        override val code get() = ForbiddenCode

        override fun equals(other: Any?) = other is Forbidden
    }

    /**
     * Representation of invalid_content_type error.
     */
    object InvalidContentType : ErrorRepresentation() {
        override val code get() = InvalidContentTypeCode
    }

    /**
     * Representation of invalid_move error.
     */
    object InvalidMove : ErrorRepresentation() {
        override val code get() = InvalidMoveCode

        override fun equals(other: Any?) = other is InvalidMove
    }

    /**
     * Representation of invalid_parameter error.
     *
     * @param parameter Parameter name.
     * @param reason Error message.
     */
    data class InvalidParameter(
        val parameter: String,
        val reason: String
    ) : ErrorRepresentation() {
        override val code get() = InvalidParameterCode
    }

    /**
     * Representation of invalid_player error.
     */
    object InvalidPlayer : ErrorRepresentation() {
        override val code get() = InvalidPlayerCode

        override fun equals(other: Any?) = other is InvalidPlayer
    }

    /**
     * Representation of invalid_request error.
     */
    object InvalidRequestBody : ErrorRepresentation() {
        override val code get() = InvalidRequestBodyCode
    }

    /**
     * Representation of method_not_allowed error.
     */
    object MethodNotAllowed : ErrorRepresentation() {
        override val code get() = MethodNotAllowedCode
    }

    /**
     * Representation of not_found error.
     */
    object NotFound : ErrorRepresentation() {
        override val code get() = NotFoundCode
    }

    /**
     * Representation of player_not_found error.
     *
     * @param playerColor Player color.
     */
    data class PlayerNotFound(
        val playerColor: PieceRepresentation.Color,
    ) : ErrorRepresentation() {
        override val code get() = PlayerNotFoundCode
    }

    /**
     * Representation of players_are_same_user error.
     */
    object PlayersAreSameUser : ErrorRepresentation() {
        override val code get() = PlayersAreSameUserCode

        override fun equals(other: Any?) = other is PlayersAreSameUser
    }

    /**
     * Representation of unauthorized error.
     */
    object Unauthorized : ErrorRepresentation() {
        override val code get() = UnauthorizedCode

        override fun equals(other: Any?) = other is Unauthorized
    }

    /**
     * Representation of unexpected_error error.
     */
    object Unexpected : ErrorRepresentation() {
        override val code get() = UnexpectedErrorCode
    }

    /**
     * Representation of unsupported_field error.
     *
     * @param supportedFields Set of supported fields.
     */
    data class UnsupportedField(
        val supportedFields: Set<String>
    ) : ErrorRepresentation() {
        override val code get() = UnsupportedFieldCode
    }

    /**
     * Representation of email_already_used error.
     *
     * @param email Email.
     */
    data class UserEmailAlreadyUsed(
        val email: String
    ) : ErrorRepresentation() {
        override val code get() = UserEmailAlreadyUsedCode
    }

    /**
     * Representation of pseudo_already_used error.
     *
     * @param pseudo Pseudo.
     */
    data class UserPseudoAlreadyUsed(
        val pseudo: String
    ) : ErrorRepresentation() {
        override val code get() = UserPseudoAlreadyUsedCode
    }

    /**
     * Representation of validation_error error.
     *
     * @param errors Errors.
     */
    data class Validation(
        val errors: Set<ErrorRepresentation>
    ) : ErrorRepresentation() {
        override val code get() = ValidationErrorCode
    }

    /**
     * Error code.
     */
    abstract val code: String
}
