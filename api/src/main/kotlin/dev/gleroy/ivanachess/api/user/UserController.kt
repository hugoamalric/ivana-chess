@file:Suppress("RegExpUnexpectedAnchor")

package dev.gleroy.ivanachess.api.user

import dev.gleroy.ivanachess.api.ApiConstants
import dev.gleroy.ivanachess.api.PageConverter
import dev.gleroy.ivanachess.api.io.UserConverter
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
 * @param userService User service.
 * @param userConverter User converter.
 * @param pageConverter Page converter.
 */
@RestController
@RequestMapping(ApiConstants.User.Path)
@Validated
class UserController(
    private val userService: UserService,
    private val userConverter: UserConverter,
    private val pageConverter: PageConverter,
    private val passwordEncoder: BCryptPasswordEncoder
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
        val user = userService.create(dto.pseudo, dto.email, passwordEncoder.encode(dto.password))
        return userConverter.convertToDto(user)
    }
}
