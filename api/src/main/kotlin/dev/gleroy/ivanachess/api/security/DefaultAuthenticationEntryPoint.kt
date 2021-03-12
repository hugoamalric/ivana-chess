package dev.gleroy.ivanachess.api.security

import com.fasterxml.jackson.databind.ObjectMapper
import dev.gleroy.ivanachess.api.ApiConstants
import dev.gleroy.ivanachess.dto.ErrorDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Default implementation of authentication entry point.
 *
 * @param mapper Object mapper.
 */
internal class DefaultAuthenticationEntryPoint(
    private val mapper: ObjectMapper
) : AuthenticationEntryPoint {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(DefaultAuthenticationEntryPoint::class.java)
    }

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        if (request.requestURI != ApiConstants.Authentication.Path) {
            Logger.warn("Anonymous user attempted to access ${request.requestURI}")
        }
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        mapper.writeValue(response.outputStream, ErrorDto.Unauthorized)
    }
}
