package dev.gleroy.ivanachess.api.security

import dev.gleroy.ivanachess.api.Properties
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * JWT implementation of authentication filter.
 *
 * @param service Authentication service.
 * @param manager Authentication manager.
 * @param props Properties.
 */
class JwtAuthenticationFilter(
    private val service: AuthenticationService,
    manager: AuthenticationManager,
    private val props: Properties
) : BasicAuthenticationFilter(manager) {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    }

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val token = token(request)
        if (token != null) {
            try {
                val jwt = service.parseJwt(token)
                try {
                    val principal = service.loadUserByUsername(jwt.pseudo)
                    SecurityContextHolder.getContext().authentication = AuthenticationAdapter(principal)
                } catch (exception: UsernameNotFoundException) {
                    Logger.warn(
                        "User '${jwt.pseudo}' attempted to access ${request.requestURI} but does not exist in database"
                    )
                }
            } catch (exception: BadJwtException) {
                Logger.warn("Anonymous user attempted to access ${request.requestURI} with bad JWT")
            }
        }
        chain.doFilter(request, response)
    }

    private fun token(req: HttpServletRequest): String? {
        val authHeaderValue = req.getHeader(props.auth.header.name)
        return if (authHeaderValue == null || !authHeaderValue.startsWith(props.auth.header.valuePrefix)) {
            req.cookies?.find { it.name == props.auth.cookie.name }?.value
        } else {
            authHeaderValue.substring(props.auth.header.valuePrefix.length)
        }
    }
}
