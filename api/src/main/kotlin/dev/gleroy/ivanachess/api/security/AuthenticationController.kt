package dev.gleroy.ivanachess.api.security

import dev.gleroy.ivanachess.api.ApiConstants
import dev.gleroy.ivanachess.api.Properties
import dev.gleroy.ivanachess.dto.LogInDto
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.Clock
import java.time.Duration
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

/**
 * Authentication API controller.
 *
 * @param service Authentication service.
 * @param clock Clock.
 * @param props Properties.
 */
@RestController
@RequestMapping(ApiConstants.Authentication.Path)
@Validated
class AuthenticationController(
    private val service: AuthenticationService,
    private val clock: Clock,
    private val props: Properties
) {
    /**
     * Generate JWT for user.
     *
     * @param dto Sign-in DTO.
     * @param response HTTP response.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logIn(@RequestBody @Valid dto: LogInDto, response: HttpServletResponse) {
        val jwt = service.generateJwt(dto.pseudo, dto.password)
        val maxAge = Duration.between(clock.instant(), jwt.expirationDate).toSeconds().toInt()
        response.addHeader(props.auth.header.name, "${props.auth.header.valuePrefix}${jwt.token}")
        response.addCookie(authenticationCookie(jwt.token, maxAge))
    }

    /**
     * Delete authentication cookie.
     *
     * @param response HTTP response.
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logOut(response: HttpServletResponse) {
        response.addCookie(authenticationCookie("", 0))
    }

    /**
     * Create authentication cookie.
     *
     * @param token Token.
     * @param maxAge Number of seconds for which the cookie is valid.
     * @return Cookie.
     */
    private fun authenticationCookie(token: String, maxAge: Int) =
        Cookie(props.auth.cookie.name, token).apply {
            domain = props.auth.cookie.domain
            secure = props.auth.cookie.secure
            isHttpOnly = props.auth.cookie.httpOnly
            this.maxAge = maxAge
        }
}
