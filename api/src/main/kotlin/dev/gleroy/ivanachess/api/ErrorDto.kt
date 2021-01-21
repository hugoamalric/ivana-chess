package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * Invalid content type error code.
 */
private const val InvalidContentTypeCode = "invalid_content_type"

/**
 * Invalid parameter error code.
 */
private const val InvalidParameterCode = "invalid_parameter"

/**
 * Invalid request body error code.
 */
private const val InvalidRequestBodyCode = "invalid_request_body"

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
    JsonSubTypes.Type(value = ErrorDto.InvalidContentType::class, name = InvalidContentTypeCode),
    JsonSubTypes.Type(value = ErrorDto.InvalidParameter::class, name = InvalidParameterCode),
    JsonSubTypes.Type(value = ErrorDto.InvalidRequestBody::class, name = InvalidRequestBodyCode),
    JsonSubTypes.Type(value = ErrorDto.Validation::class, name = ValidationErrorCode),
)
sealed class ErrorDto {
    /**
     * Invalid content type.
     */
    object InvalidContentType : ErrorDto() {
        override val code = InvalidContentTypeCode
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
     * Invalid request body.
     */
    object InvalidRequestBody : ErrorDto() {
        override val code = InvalidRequestBodyCode
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
