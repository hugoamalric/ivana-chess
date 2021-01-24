package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import dev.gleroy.ivanachess.core.InvalidMoveException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException
import javax.servlet.http.HttpServletRequest

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
     * Handle GameNotFound exception.
     *
     * @param exception Exception.
     * @param request Request.
     * @return Error DTO.
     */
    @ExceptionHandler(PlayException.GameNotFound::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleGameNotFound(exception: PlayException.GameNotFound, request: HttpServletRequest) =
        ErrorDto.GameNotFound.apply {
            Logger.debug(
                "Client ${request.remoteAddr} attempted to access game which does not exist " +
                        "from token ${exception.token}"
            )
        }

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
                errors = setOf(ErrorDto.InvalidParameter(cause.path.toHumanReadablePath(), "must not be null"))
            )
            else -> ErrorDto.InvalidRequestBody
        }.apply {
            Logger.debug("Client ${request.remoteAddr} sent invalid request body on ${request.requestURI}")
        }

    /**
     * Handle InvalidMove exception.
     *
     * @param exception Exception.
     * @param request Request.
     * @return Error DTO.
     */
    @ExceptionHandler(PlayException.InvalidMove::class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    fun handleInvalidMove(exception: PlayException.InvalidMove, request: HttpServletRequest) =
        ErrorDto.InvalidMove(exception.cause.message).apply {
            Logger.debug(
                "Client ${request.remoteAddr} (${exception.color}) attempted " +
                    "to play invalid move in game ${exception.id}"
            )
        }

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
     * Handle InvalidPlayer exception.
     *
     * @param exception Exception.
     * @param request Request.
     * @return Error DTO.
     */
    @ExceptionHandler(PlayException.InvalidPlayer::class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    fun handleInvalidPlayer(exception: PlayException.InvalidPlayer, request: HttpServletRequest) =
        ErrorDto.InvalidPlayer.apply {
            Logger.debug(
                "Client ${request.remoteAddr} (${exception.color}) attempted " +
                        "to steal turn in game ${exception.id}"
            )
        }

    /**
     * Convert list of JSON reference to string path.
     *
     * @return String path.
     */
    private fun List<JsonMappingException.Reference>.toHumanReadablePath() = map { it.fieldName }
        .reduce { acc, fieldName -> "$acc.$fieldName" }
}
