package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import dev.gleroy.ivanachess.api.game.*
import dev.gleroy.ivanachess.api.user.UserEmailAlreadyUsedException
import dev.gleroy.ivanachess.api.user.UserPseudoAlreadyUsedException
import dev.gleroy.ivanachess.dto.ErrorDto
import dev.gleroy.ivanachess.dto.PieceDto
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
     * @return Error DTO.
     */
    @ExceptionHandler(BindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBindException(exception: BindException) = ErrorDto.Validation(
        errors = exception.bindingResult.fieldErrors
            .map { error ->
                ErrorDto.InvalidParameter(
                    parameter = error.field,
                    reason = error.defaultMessage!!
                )
            }
            .toSet()
    ).apply { Logger.debug(exception.message, exception) }

    /**
     * Handle BadCredentialsException.
     *
     * @return Error DTO.
     */
    @ExceptionHandler(BadCredentialsException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleBadCredentialsException() = ErrorDto.BadCredentials

    /**
     * Handle ConstraintViolationException.
     *
     * @param exception Exception.
     * @return Error DTO.
     */
    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolation(exception: ConstraintViolationException) = ErrorDto.Validation(
        errors = exception.constraintViolations
            .map { violation ->
                ErrorDto.InvalidParameter(
                    parameter = violation.propertyPath.toHumanReadablePath(),
                    reason = violation.message
                )
            }
            .toSet()
    ).apply { Logger.debug(exception.message, exception) }

    /**
     * Handle EntityNotFoundException.
     *
     * @return Error DTO.
     */
    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleEntityNotFound() = ErrorDto.EntityNotFound

    /**
     * Handle HttpMediaTypeNotSupportedException
     *
     * @param exception Exception.
     * @return Error DTO.
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    fun handleHttpMediaTypeNotSupported(exception: HttpMediaTypeNotSupportedException) =
        ErrorDto.InvalidContentType.apply { Logger.debug(exception.message, exception) }

    /**
     * Handle HttpMessageNotReadableException.
     *
     * @param exception Exception.
     * @return Error DTO.
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadable(exception: HttpMessageNotReadableException) =
        when (val cause = exception.cause) {
            is MissingKotlinParameterException -> ErrorDto.Validation(
                errors = setOf(
                    ErrorDto.InvalidParameter(
                        parameter = cause.path.toHumanReadablePath(),
                        reason = "must not be null"
                    )
                )
            )
            else -> ErrorDto.InvalidRequestBody
        }.apply { Logger.debug(exception.message, exception) }

    /**
     * Handle HttpRequestMethodNotSupportedException.
     *
     * @param exception Exception.
     * @return Error DTO.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    fun handleHttpRequestMethodNotSupported(exception: HttpRequestMethodNotSupportedException) =
        ErrorDto.MethodNotAllowed.apply { Logger.debug(exception.message, exception) }

    /**
     * Handle InvalidMoveException.
     *
     * @param exception Exception.
     * @return Error DTO.
     */
    @ExceptionHandler(InvalidMoveException::class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    fun handleInvalidMove(exception: InvalidMoveException) = ErrorDto.InvalidMove

    /**
     * Handle InvalidPlayerException.
     *
     * @return Error DTO.
     */
    @ExceptionHandler(InvalidPlayerException::class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    fun handleInvalidPlayer() = ErrorDto.InvalidPlayer

    /**
     * Handle MethodArgumentNotValidException.
     *
     * @param exception Exception.
     * @return Error DTO.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValid(exception: MethodArgumentNotValidException) = ErrorDto.Validation(
        errors = exception.fieldErrors.map { ErrorDto.InvalidParameter(it.field, it.defaultMessage!!) }.toSet()
    ).apply { Logger.debug(exception.message, exception) }

    /**
     * Handle MethodArgumentTypeMismatchException.
     *
     * @param exception Exception.
     * @return Error DTO.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentTypeMismatch(exception: MethodArgumentTypeMismatchException) =
        ErrorDto.InvalidParameter(
            parameter = exception.parameter.parameterName!!,
            reason = "must be ${exception.requiredType}"
        ).apply { Logger.debug(exception.message, exception) }

    /**
     * Handle MissingServletRequestParameterException.
     *
     * @param exception Exception.
     * @return Error DTO.
     */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingServletRequestParameter(exception: MissingServletRequestParameterException) = ErrorDto.Validation(
        errors = setOf(
            ErrorDto.InvalidParameter(
                parameter = exception.parameterName,
                reason = "must not be null"
            )
        )
    ).apply { Logger.debug(exception.message, exception) }

    /**
     * Handle NoHandlerFoundException.
     *
     * @return Error DTO.
     */
    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound() = ErrorDto.NotFound

    /**
     * Handle NotAllowedPlayerException.
     *
     * @return Error DTO.
     */
    @ExceptionHandler(NotAllowedPlayerException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleNotAllowedPlayer() = ErrorDto.Forbidden

    /**
     * Handle PlayerNotFoundException.
     *
     * @param exception Exception.
     * @return Error DTO.
     */
    @ExceptionHandler(PlayerNotFoundException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handlePlayerNotFound(exception: PlayerNotFoundException) = when (exception) {
        is PlayerNotFoundException.White -> ErrorDto.PlayerNotFound(PieceDto.Color.White)
        is PlayerNotFoundException.Black -> ErrorDto.PlayerNotFound(PieceDto.Color.Black)
    }

    /**
     * Handle PlayersAreSameUserException.
     *
     * @return Error DTO.
     */
    @ExceptionHandler(PlayersAreSameUserException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handlePlayersAreSameUser() = ErrorDto.PlayersAreSameUser

    /**
     * Handle unexpected exceptions.
     *
     * @param exception Exception.
     * @return Error DTO.
     */
    @ExceptionHandler(Throwable::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleUnexpectedException(exception: Throwable) = ErrorDto.Unexpected.apply {
        Logger.error(exception.message, exception)
    }

    /**
     * Handle UnsupportedFieldException.
     *
     * @param exception Exception.
     * @return Error DTO.
     */
    @ExceptionHandler(UnsupportedFieldException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleUnsupportedField(exception: UnsupportedFieldException) = ErrorDto.Validation(
        errors = setOf(
            ErrorDto.UnsupportedField(
                supportedFields = exception.supportedFields.map { it.label }.toSet()
            )
        )
    )

    /**
     * Handle UserEmailAlreadyUsedException.
     *
     * @param exception Exception.
     * @return Error DTO.
     */
    @ExceptionHandler(UserEmailAlreadyUsedException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleUserEmailAlreadyUsed(exception: UserEmailAlreadyUsedException) =
        ErrorDto.UserEmailAlreadyUsed(exception.email)

    /**
     * Handle UserPseudoAlreadyUsedException.
     *
     * @param exception Exception.
     * @return Error DTO.
     */
    @ExceptionHandler(UserPseudoAlreadyUsedException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleUserPseudoAlreadyUsed(exception: UserPseudoAlreadyUsedException) =
        ErrorDto.UserPseudoAlreadyUsed(exception.pseudo)

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
