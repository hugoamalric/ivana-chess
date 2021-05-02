package dev.gleroy.ivanachess.api.security

import dev.gleroy.ivanachess.api.AbstractController
import dev.gleroy.ivanachess.api.Properties
import dev.gleroy.ivanachess.io.ApiConstants
import dev.gleroy.ivanachess.io.Credentials
import dev.gleroy.ivanachess.io.UserConverter
import dev.gleroy.ivanachess.io.UserRepresentation
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.Clock
import java.time.Duration
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

/**
 * Authentication API controller.
 *
 * @param service Authentication service.
 * @param userConverter User converter.
 * @param clock Clock.
 * @param props Properties.
 */
@RestController
@RequestMapping(ApiConstants.Authentication.Path)
@Validated
class AuthenticationController(
    private val service: AuthenticationService,
    private val userConverter: UserConverter,
    private val clock: Clock,
    override val props: Properties,
) : AbstractController() {
    /**
     * Generate JWT for user.
     *
     * @param creds Credentials.
     * @param response HTTP response.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logIn(@RequestBody @Valid creds: Credentials, response: HttpServletResponse) {
        val jwt = service.generateJwt(creds.pseudo, creds.password)
        val maxAge = Duration.between(clock.instant(), jwt.expirationDate).toSeconds().toInt()
        response.addHeader(props.auth.header.name, "${props.auth.header.valuePrefix}${jwt.token}")
        response.addCookie(createAuthenticationCookie(jwt.token, maxAge))
    }

    /**
     * Delete authentication cookie.
     *
     * @param response HTTP response.
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logOut(response: HttpServletResponse) {
        response.addCookie(expireAuthenticationCookie())
    }

    /**
     * Get current authenticated user.
     *
     * @param auth Authentication.
     * @return Representation of user.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun me(auth: Authentication): UserRepresentation {
        return userConverter.convertToRepresentation(authenticatedUser(auth))
    }
}
