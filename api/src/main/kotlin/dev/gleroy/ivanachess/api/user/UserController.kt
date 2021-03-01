@file:Suppress("RegExpUnexpectedAnchor")

package dev.gleroy.ivanachess.api.user

import dev.gleroy.ivanachess.api.ApiConstants
import dev.gleroy.ivanachess.api.PageConverter
import dev.gleroy.ivanachess.api.Properties
import dev.gleroy.ivanachess.api.security.AuthenticationService
import dev.gleroy.ivanachess.dto.LogInDto
import dev.gleroy.ivanachess.dto.UserDto
import dev.gleroy.ivanachess.dto.UserSubscriptionDto
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.Clock
import java.time.Duration
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

/**
 * User API controller.
 *
 * @param userService User service.
 * @param authService Authentication service.
 * @param userConverter User converter.
 * @param pageConverter Page converter.
 * @param clock Clock.
 * @param props Properties.
 */
@RestController
@RequestMapping(ApiConstants.User.Path)
@Validated
class UserController(
    private val userService: UserService,
    private val authService: AuthenticationService,
    private val userConverter: UserConverter,
    private val pageConverter: PageConverter,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val clock: Clock,
    private val props: Properties
) {
    /**
     * Generate JWT for user.
     *
     * @param dto Sign-in DTO.
     */
    @PostMapping(ApiConstants.User.LogInPath)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logIn(@RequestBody @Valid dto: LogInDto, response: HttpServletResponse) {
        val jwt = authService.generateJwt(dto.pseudo, dto.password)
        val cookie = Cookie(props.auth.cookie.name, jwt.token).apply {
            domain = props.auth.cookie.domain
            secure = props.auth.cookie.secure
            isHttpOnly = props.auth.cookie.httpOnly
            maxAge = Duration.between(clock.instant(), jwt.expirationDate).toSeconds().toInt()
        }
        response.addHeader(props.auth.header.name, "${props.auth.header.valuePrefix}${jwt.token}")
        response.addCookie(cookie)
    }

    /**
     * Create new user from subscription.
     *
     * @param dto User subscription DTO.
     * @return User DTO.
     */
    @PostMapping(ApiConstants.User.SignUpPath)
    @ResponseStatus(HttpStatus.CREATED)
    fun signUp(@RequestBody @Valid dto: UserSubscriptionDto): UserDto {
        val user = userService.create(dto.pseudo, dto.email, passwordEncoder.encode(dto.password))
        return userConverter.convert(user)
    }
}
