@file:Suppress("RegExpUnexpectedAnchor")

package dev.gleroy.ivanachess.api.user

import dev.gleroy.ivanachess.api.ApiConstants
import dev.gleroy.ivanachess.api.PageConverter
import dev.gleroy.ivanachess.api.Properties
import dev.gleroy.ivanachess.dto.UserDto
import dev.gleroy.ivanachess.dto.UserSubscriptionDto
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * User API controller.
 *
 * @param service User service.
 * @param userConverter User converter.
 * @param pageConverter Page converter.
 * @param props Properties.
 */
@RestController
@RequestMapping(ApiConstants.User.Path)
@Validated
class UserController(
    private val service: UserService,
    private val userConverter: UserConverter,
    private val pageConverter: PageConverter,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val props: Properties
) {
    /**
     * Create new user from subscription.
     *
     * @param dto User subscription DTO.
     * @return User DTO.
     */
    @PostMapping(ApiConstants.User.SignUpPath)
    @ResponseStatus(HttpStatus.CREATED)
    fun signUp(@RequestBody @Valid dto: UserSubscriptionDto): UserDto {
        val user = service.create(dto.pseudo, passwordEncoder.encode(dto.password), User.Role.Simple)
        return userConverter.convert(user)
    }
}
