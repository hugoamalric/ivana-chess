package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import dev.gleroy.ivanachess.api.game.*
import dev.gleroy.ivanachess.api.user.UserEmailAlreadyUsedException
import dev.gleroy.ivanachess.api.user.UserPseudoAlreadyUsedException
import dev.gleroy.ivanachess.dto.ErrorDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import javax.servlet.http.HttpServletRequest
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
     * Handle BadCredentials exception.
     *
     * @return Error DTO.
     */
    @ExceptionHandler(BadCredentialsException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleBadCredentials() = ErrorDto.Unauthorized

    /**
     * Handle ConstraintViolation exception.
     *
     * @param exception Exception.
     * @param request Request.
     * @return Error DTO.
     */
    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolation(exception: ConstraintViolationException, request: HttpServletRequest) =
        ErrorDto.Validation(
            errors = exception.constraintViolations
                .map { violation ->
                    ErrorDto.InvalidParameter(
                        parameter = violation.propertyPath.toHumanReadablePath(),
                        reason = violation.message
                    )
                }
                .toSet()
        ).apply {
            Logger.debug(
                "Client ${request.remoteAddr} sent invalid request parameters on ${request.requestURI}"
            )
        }

    /**
     * Handle GameNotFound exceptions.
     *
     * @return Error DTO.
     */
    @ExceptionHandler(GameNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleGameNotFound() = ErrorDto.GameNotFound

    /**
     * Handle HttpMediaTypeNotSupported exception.
     *
     * @param exception Exception.
     * @param request Request.
     * @return Error DTO.
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    fun handleHttpMediaTypeNotSupported(exception: HttpMediaTypeNotSupportedException, request: HttpServletRequest) =
        ErrorDto.InvalidContentType.apply {
            Logger.debug(
                "Client ${request.remoteAddr} sent invalid content type ('${exception.contentType}') " +
                        "on ${request.requestURI}"
            )
        }

    /**
     * Handle HttpMessageNotReadableException exception.
     *
     * @param request Request.
     * @return Error DTO.
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadable(exception: HttpMessageNotReadableException, request: HttpServletRequest) =
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
        }.apply {
            Logger.debug("Client ${request.remoteAddr} sent invalid request body on ${request.requestURI}")
        }

    /**
     * Handle HttpMessageNotReadableException exception.
     *
     * @param exception Exception.
     * @param request Request.
     * @return Error DTO.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    fun handleHttpRequestMethodNotSupported(
        exception: HttpRequestMethodNotSupportedException,
        request: HttpServletRequest
    ) = ErrorDto.MethodNotAllowed.apply {
        Logger.debug(
            "Client ${request.remoteAddr} attempted to perform ${exception.method} " +
                    "on ${request.requestURI}"
        )
    }

    /**
     * Handle InvalidMove exception.
     *
     * @param exception Exception.
     * @return Error DTO.
     */
    @ExceptionHandler(InvalidMoveException::class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    fun handleInvalidMove(exception: InvalidMoveException) = ErrorDto.InvalidMove

    /**
     * Handle InvalidPlayer exception.
     *
     * @return Error DTO.
     */
    @ExceptionHandler(InvalidPlayerException::class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    fun handleInvalidPlayer() = ErrorDto.InvalidPlayer

    /**
     * Handle HttpMediaTypeNotSupported exception.
     *
     * @param exception Exception.
     * @param request Request.
     * @return Error DTO.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValid(exception: MethodArgumentNotValidException, request: HttpServletRequest) =
        ErrorDto.Validation(
            errors = exception.fieldErrors.map { ErrorDto.InvalidParameter(it.field, it.defaultMessage!!) }.toSet()
        ).apply {
            Logger.debug(
                "Client ${request.remoteAddr} sent request body with invalid parameters on ${request.requestURI}"
            )
        }

    /**
     * Handle MethodArgumentTypeMismatch exception.
     *
     * @param exception Exception.
     * @param request Request.
     * @return Error DTO.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentTypeMismatch(exception: MethodArgumentTypeMismatchException, request: HttpServletRequest) =
        ErrorDto.InvalidParameter(
            parameter = exception.parameter.parameterName!!,
            reason = "must be ${exception.requiredType}"
        ).apply {
            Logger.debug(
                "Client ${request.remoteAddr} sent invalid ${exception.parameter.parameterName} request parameter " +
                        "on ${request.requestURI}"
            )
        }

    /**
     * Handle NoHandlerFound exception.
     *
     * @param request Request.
     * @return Error DTO.
     */
    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(request: HttpServletRequest) = ErrorDto.NotFound.apply {
        Logger.debug("Client ${request.remoteAddr} attempted to access ${request.requestURI} (not found)")
    }

    /**
     * Handle NotAllowedPlayer exception.
     *
     * @return Error DTO.
     */
    @ExceptionHandler(NotAllowedPlayerException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleNotAllowedPlayer() = ErrorDto.Forbidden

    /**
     * Handle PlayerNotFound exception.
     *
     * @param exception Exception.
     * @return Error DTO.
     */
    @ExceptionHandler(PlayerNotFoundException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handlePlayerNotFound(exception: PlayerNotFoundException) = ErrorDto.PlayerNotFound(exception.id)

    /**
     * Handle other exceptions.
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
     * Handle UserEmailAlreadyUsed exception.
     *
     * @param exception Exception.
     * @return Error DTO.
     */
    @ExceptionHandler(UserEmailAlreadyUsedException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleUserEmailAlreadyUsed(exception: UserEmailAlreadyUsedException) =
        ErrorDto.UserEmailAlreadyUsed(exception.email)

    /**
     * Handle UserPseudoAlreadyUsed exception.
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
