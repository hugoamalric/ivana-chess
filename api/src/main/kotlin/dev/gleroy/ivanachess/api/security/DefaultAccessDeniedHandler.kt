package dev.gleroy.ivanachess.api.security

import com.fasterxml.jackson.databind.ObjectMapper
import dev.gleroy.ivanachess.dto.ErrorDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.access.AccessDeniedHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Default implementation of access defined handler.
 *
 * @param mapper Object mapper.
 */
internal class DefaultAccessDeniedHandler(
    private val mapper: ObjectMapper
) : AccessDeniedHandler {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(DefaultAccessDeniedHandler::class.java)
    }

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        val principal = SecurityContextHolder.getContext().authentication.principal as UserDetailsAdapter
        Logger.warn("User '${principal.username}' attempted to access ${request.requestURI}")
        response.status = HttpStatus.FORBIDDEN.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        mapper.writeValue(response.outputStream, ErrorDto.Forbidden)
    }
}
