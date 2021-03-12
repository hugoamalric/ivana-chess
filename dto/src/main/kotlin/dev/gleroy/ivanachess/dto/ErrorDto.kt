package dev.gleroy.ivanachess.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.util.*

/**
 * Bad credentials error code.
 */
private const val BadCredentialsCode = "bad_credentials"

/**
 * Forbidden error code.
 */
private const val ForbiddenCode = "forbidden"

/**
 * Game not found error code.
 */
private const val GameNotFoundCode = "game_not_found"

/**
 * Invalid content type error code.
 */
private const val InvalidContentTypeCode = "invalid_content_type"

/**
 * Invalid move code.
 */
private const val InvalidMoveCode = "invalid_move"

/**
 * Invalid parameter error code.
 */
private const val InvalidParameterCode = "invalid_parameter"

/**
 * Invalid player code.
 */
private const val InvalidPlayerCode = "invalid_player"

/**
 * Invalid request body error code.
 */
private const val InvalidRequestBodyCode = "invalid_request_body"

/**
 * Method not allowed error code.
 */
private const val MethodNotAllowedCode = "method_not_allowed"

/**
 * Not found error code.
 */
private const val NotFoundCode = "not_found"

/**
 * Player not found error code.
 */
private const val PlayerNotFoundCode = "player_not_found"

/**
 * Unauthorized error code.
 */
private const val UnauthorizedCode = "unauthorized"

/**
 * Unexpected error code.
 */
private const val UnexpectedErrorCode = "unexpected_error"

/**
 * Unsupported field code.
 */
private const val UnsupportedFieldCode = "unsupported_field"

/**
 * User email already used error code.
 */
private const val UserEmailAlreadyUsedCode = "email_already_used"

/**
 * User pseudo already used error code.
 */
private const val UserPseudoAlreadyUsedCode = "pseudo_already_used"

/**
 * Validation error code.
 */
private const val ValidationErrorCode = "validation_error"

/**
 * Error DTO.
 */
@JsonTypeInfo(
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    use = JsonTypeInfo.Id.NAME,
    property = "code"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ErrorDto.BadCredentials::class, name = BadCredentialsCode),
    JsonSubTypes.Type(value = ErrorDto.Forbidden::class, name = ForbiddenCode),
    JsonSubTypes.Type(value = ErrorDto.GameNotFound::class, name = GameNotFoundCode),
    JsonSubTypes.Type(value = ErrorDto.InvalidContentType::class, name = InvalidContentTypeCode),
    JsonSubTypes.Type(value = ErrorDto.InvalidMove::class, name = InvalidMoveCode),
    JsonSubTypes.Type(value = ErrorDto.InvalidParameter::class, name = InvalidParameterCode),
    JsonSubTypes.Type(value = ErrorDto.InvalidPlayer::class, name = InvalidPlayerCode),
    JsonSubTypes.Type(value = ErrorDto.InvalidRequestBody::class, name = InvalidRequestBodyCode),
    JsonSubTypes.Type(value = ErrorDto.MethodNotAllowed::class, name = MethodNotAllowedCode),
    JsonSubTypes.Type(value = ErrorDto.NotFound::class, name = NotFoundCode),
    JsonSubTypes.Type(value = ErrorDto.PlayerNotFound::class, name = PlayerNotFoundCode),
    JsonSubTypes.Type(value = ErrorDto.Unauthorized::class, name = UnauthorizedCode),
    JsonSubTypes.Type(value = ErrorDto.Unexpected::class, name = UnexpectedErrorCode),
    JsonSubTypes.Type(value = ErrorDto.UnsupportedField::class, name = UnsupportedFieldCode),
    JsonSubTypes.Type(value = ErrorDto.UserEmailAlreadyUsed::class, name = UserEmailAlreadyUsedCode),
    JsonSubTypes.Type(value = ErrorDto.UserPseudoAlreadyUsed::class, name = UserPseudoAlreadyUsedCode),
    JsonSubTypes.Type(value = ErrorDto.Validation::class, name = ValidationErrorCode),
)
sealed class ErrorDto {
    /**
     * Bad credentials.
     */
    object BadCredentials : ErrorDto() {
        override val code = BadCredentialsCode
    }

    /**
     * Forbidden.
     */
    object Forbidden : ErrorDto() {
        override val code = ForbiddenCode
    }

    /**
     * Game not found.
     */
    object GameNotFound : ErrorDto() {
        override val code = GameNotFoundCode
    }

    /**
     * Invalid content type.
     */
    object InvalidContentType : ErrorDto() {
        override val code = InvalidContentTypeCode
    }

    /**
     * Invalid move.
     */
    object InvalidMove : ErrorDto() {
        override val code = InvalidMoveCode
    }

    /**
     * Invalid parameter.
     *
     * @param parameter Parameter name.
     * @param reason Error message.
     */
    data class InvalidParameter(
        val parameter: String,
        val reason: String
    ) : ErrorDto() {
        override val code = InvalidParameterCode
    }

    /**
     * Invalid player.
     */
    object InvalidPlayer : ErrorDto() {
        override val code = InvalidPlayerCode
    }

    /**
     * Invalid request body.
     */
    object InvalidRequestBody : ErrorDto() {
        override val code = InvalidRequestBodyCode
    }

    /**
     * Method not allowed.
     */
    object MethodNotAllowed : ErrorDto() {
        override val code = MethodNotAllowedCode
    }

    /**
     * Not found.
     */
    object NotFound : ErrorDto() {
        override val code = NotFoundCode
    }

    /**
     * Player not found.
     *
     * @param id User ID.
     */
    data class PlayerNotFound(
        val id: UUID
    ) : ErrorDto() {
        override val code = PlayerNotFoundCode
    }

    /**
     * Unauthorized.
     */
    object Unauthorized : ErrorDto() {
        override val code = UnauthorizedCode
    }

    /**
     * Unexpected.
     */
    object Unexpected : ErrorDto() {
        override val code = UnexpectedErrorCode
    }

    /**
     * Unsupported field.
     *
     * @param supportedFields Set of supported fields.
     */
    data class UnsupportedField(
        val supportedFields: Set<String>
    ) : ErrorDto() {
        override val code = UnsupportedFieldCode
    }

    /**
     * User email already used.
     *
     * @param email Email.
     */
    data class UserEmailAlreadyUsed(
        val email: String
    ) : ErrorDto() {
        override val code = UserEmailAlreadyUsedCode
    }

    /**
     * User pseudo already used.
     *
     * @param pseudo Pseudo.
     */
    data class UserPseudoAlreadyUsed(
        val pseudo: String
    ) : ErrorDto() {
        override val code = UserPseudoAlreadyUsedCode
    }

    /**
     * Validation error DTO.
     *
     * @param errors Errors.
     */
    data class Validation(
        val errors: Set<ErrorDto>
    ) : ErrorDto() {
        override val code = ValidationErrorCode
    }

    /**
     * Error code.
     */
    abstract val code: String
}
