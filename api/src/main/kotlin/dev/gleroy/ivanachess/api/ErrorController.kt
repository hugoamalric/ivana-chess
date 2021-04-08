package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import dev.gleroy.ivanachess.core.*
import dev.gleroy.ivanachess.io.ErrorRepresentation
import dev.gleroy.ivanachess.io.PieceRepresentation
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.BindException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import javax.validation.ConstraintViolationException
import javax.validation.Path

/**
 * Error controller.
 */
@RestControllerAdvice
class ErrorController {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(ErrorController::class.java)
    }

    /**
     * Handle BindException.
     *
     * @param exception Exception.
     * @return Representation of error.
     */
    @ExceptionHandler(BindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBindException(exception: BindException) = ErrorRepresentation.Validation(
        errors = exception.bindingResult.fieldErrors
            .map { error ->
                ErrorRepresentation.InvalidParameter(
                    parameter = error.field,
                    reason = error.defaultMessage!!
                )
            }
            .toSet()
    ).apply { Logger.debug(exception.message, exception) }

    /**
     * Handle BadCredentialsException.
     *
     * @return Representation of error.
     */
    @ExceptionHandler(BadCredentialsException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleBadCredentialsException() = ErrorRepresentation.BadCredentials

    /**
     * Handle ConstraintViolationException.
     *
     * @param exception Exception.
     * @return Representation of error.
     */
    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolationException(exception: ConstraintViolationException) = ErrorRepresentation.Validation(
        errors = exception.constraintViolations
            .map { violation ->
                ErrorRepresentation.InvalidParameter(
                    parameter = violation.propertyPath.toHumanReadablePath(),
                    reason = violation.message
                )
            }
            .toSet()
    ).apply { Logger.debug(exception.message, exception) }

    /**
     * Handle EntityNotFoundException.
     *
     * @return Representation of error.
     */
    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleEntityNotFoundException() = ErrorRepresentation.EntityNotFound

    /**
     * Handle HttpMediaTypeNotSupportedException
     *
     * @param exception Exception.
     * @return Representation of error.
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    fun handleHttpMediaTypeNotSupportedException(exception: HttpMediaTypeNotSupportedException) =
        ErrorRepresentation.InvalidContentType.apply { Logger.debug(exception.message, exception) }

    /**
     * Handle HttpMessageNotReadableException.
     *
     * @param exception Exception.
     * @return Representation of error.
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadableException(exception: HttpMessageNotReadableException) =
        when (val cause = exception.cause) {
            is MissingKotlinParameterException -> ErrorRepresentation.Validation(
                errors = setOf(
                    ErrorRepresentation.InvalidParameter(
                        parameter = cause.path.toHumanReadablePath(),
                        reason = "must not be null"
                    )
                )
            )
            else -> ErrorRepresentation.InvalidRequestBody
        }.apply { Logger.debug(exception.message, exception) }

    /**
     * Handle HttpRequestMethodNotSupportedException.
     *
     * @param exception Exception.
     * @return Representation of error.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    fun handleHttpRequestMethodNotSupportedException(exception: HttpRequestMethodNotSupportedException) =
        ErrorRepresentation.MethodNotAllowed.apply { Logger.debug(exception.message, exception) }

    /**
     * Handle InvalidMoveException.
     *
     * @param exception Exception.
     * @return Representation of error.
     */
    @ExceptionHandler(InvalidMoveException::class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    fun handleInvalidMoveException(exception: InvalidMoveException) = ErrorRepresentation.InvalidMove

    /**
     * Handle InvalidPlayerException.
     *
     * @return Representation of error.
     */
    @ExceptionHandler(InvalidPlayerException::class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    fun handleInvalidPlayerException() = ErrorRepresentation.InvalidPlayer

    /**
     * Handle MethodArgumentNotValidException.
     *
     * @param exception Exception.
     * @return Representation of error.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(exception: MethodArgumentNotValidException) =
        ErrorRepresentation.Validation(
            errors = exception.fieldErrors.map { ErrorRepresentation.InvalidParameter(it.field, it.defaultMessage!!) }
                .toSet()
        ).apply { Logger.debug(exception.message, exception) }

    /**
     * Handle MethodArgumentTypeMismatchException.
     *
     * @param exception Exception.
     * @return Representation of error.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentTypeMismatchException(exception: MethodArgumentTypeMismatchException) =
        ErrorRepresentation.InvalidParameter(
            parameter = exception.parameter.parameterName!!,
            reason = "must be ${exception.requiredType}"
        ).apply { Logger.debug(exception.message, exception) }

    /**
     * Handle MissingServletRequestParameterException.
     *
     * @param exception Exception.
     * @return Representation of error.
     */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingServletRequestParameterException(exception: MissingServletRequestParameterException) =
        ErrorRepresentation.Validation(
            errors = setOf(
                ErrorRepresentation.InvalidParameter(
                    parameter = exception.parameterName,
                    reason = "must not be null"
                )
            )
        ).apply { Logger.debug(exception.message, exception) }

    /**
     * Handle NoHandlerFoundException.
     *
     * @return Representation of error.
     */
    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoHandlerFoundException() = ErrorRepresentation.NotFound

    /**
     * Handle NotAllowedPlayerException.
     *
     * @return Representation of error.
     */
    @ExceptionHandler(NotAllowedPlayerException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleNotAllowedPlayerException() = ErrorRepresentation.Forbidden

    /**
     * Handle PlayerNotFoundException.
     *
     * @param exception Exception.
     * @return Representation of error.
     */
    @ExceptionHandler(PlayerNotFoundException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handlePlayerNotFoundException(exception: PlayerNotFoundException) = when (exception) {
        is PlayerNotFoundException.White -> ErrorRepresentation.PlayerNotFound(PieceRepresentation.Color.White)
        is PlayerNotFoundException.Black -> ErrorRepresentation.PlayerNotFound(PieceRepresentation.Color.Black)
    }

    /**
     * Handle PlayersAreSameUserException.
     *
     * @return Representation of error.
     */
    @ExceptionHandler(PlayersAreSameUserException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handlePlayersAreSameUserException() = ErrorRepresentation.PlayersAreSameUser

    /**
     * Handle unexpected exceptions.
     *
     * @param exception Exception.
     * @return Representation of error.
     */
    @ExceptionHandler(Throwable::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleUnexpectedException(exception: Throwable) = ErrorRepresentation.Unexpected.apply {
        Logger.error(exception.message, exception)
    }

    /**
     * Handle UnsupportedFieldException.
     *
     * @param exception Exception.
     * @return Representation of error.
     */
    @ExceptionHandler(UnsupportedFieldException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleUnsupportedFieldException(exception: UnsupportedFieldException) = ErrorRepresentation.Validation(
        errors = setOf(
            ErrorRepresentation.UnsupportedField(
                supportedFields = exception.supportedFields.map { it.label }.toSet()
            )
        )
    )

    /**
     * Handle UserEmailAlreadyUsedException.
     *
     * @param exception Exception.
     * @return Representation of error.
     */
    @ExceptionHandler(UserEmailAlreadyUsedException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleUserEmailAlreadyUsedException(exception: UserEmailAlreadyUsedException) =
        ErrorRepresentation.UserEmailAlreadyUsed(exception.email)

    /**
     * Handle UserPseudoAlreadyUsedException.
     *
     * @param exception Exception.
     * @return Representation of error.
     */
    @ExceptionHandler(UserPseudoAlreadyUsedException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleUserPseudoAlreadyUsedException(exception: UserPseudoAlreadyUsedException) =
        ErrorRepresentation.UserPseudoAlreadyUsed(exception.pseudo)

    /**
     * Convert list of JSON reference to string path.
     *
     * @return String path.
     */
    private fun List<JsonMappingException.Reference>.toHumanReadablePath() = map { it.fieldName }
        .reduce { acc, fieldName -> "$acc.$fieldName" }

    /**
     * Convert path to string path.
     *
     * @return String path.
     */
    private fun Path.toHumanReadablePath() = drop(1)
        .map { it.name }
        .reduce { acc, name -> "$acc.$name" }
}
