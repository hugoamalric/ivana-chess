@file:Suppress("RegExpUnexpectedAnchor")

package dev.gleroy.ivanachess.api.user

import dev.gleroy.ivanachess.api.ApiConstants
import dev.gleroy.ivanachess.api.UnsupportedFieldException
import dev.gleroy.ivanachess.api.io.PageConverter
import dev.gleroy.ivanachess.api.io.UserConverter
import dev.gleroy.ivanachess.dto.ExistsDto
import dev.gleroy.ivanachess.dto.UserDto
import dev.gleroy.ivanachess.dto.UserSubscriptionDto
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.Min

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
    private companion object {
        /**
         * Map which associates field to function which returns if an user exists.
         */
        private val ExistsFieldToFunction = mapOf(
            "pseudo" to { value: String, service: UserService -> service.existsByPseudo(value) },
            "email" to { value: String, service: UserService -> service.existsByEmail(value) }
        )

        /**
         * User entity type.
         */
        private const val UserEntityType = "user"
    }

    /**
     * Check if user exists by pseudo or password.
     *
     * @param by Field used to search user.
     * @param value Value of the field to search.
     * @return Exists DTO.
     * @throws UnsupportedFieldException If field is unsupported.
     */
    @GetMapping(ApiConstants.ExistsPath)
    @ResponseStatus(HttpStatus.OK)
    @Throws(UnsupportedFieldException::class)
    fun exists(
        @RequestParam(ApiConstants.QueryParams.By) by: String,
        @RequestParam(ApiConstants.QueryParams.Value) value: String
    ): ExistsDto {
        val field = by.toLowerCase()
        val fn = ExistsFieldToFunction[field] ?: throw UnsupportedFieldException(
            field = field,
            entityType = UserEntityType,
            supportedFields = ExistsFieldToFunction.keys
        )
        return ExistsDto(fn(value, userService))
    }

    /**
     * Search user by pseudo.
     *
     * @param q Part of pseudo to search.
     * @param maxSize Maximum size of returned list.
     * @return DTO which match search.
     */
    @GetMapping(ApiConstants.SearchPath)
    @ResponseStatus(HttpStatus.OK)
    fun searchByPseudo(
        @RequestParam(ApiConstants.QueryParams.Q) q: String,
        @RequestParam(name = ApiConstants.QueryParams.MaxSize, required = false, defaultValue = "5")
        @Min(1)
        maxSize: Int,
        @RequestParam(ApiConstants.QueryParams.Exclude, required = false) excluding: Set<UUID>?
    ): List<UserDto> {
        val users = userService.searchByPseudo(q, maxSize, excluding ?: emptySet())
        return users.map { userConverter.convertToDto(it) }
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
        return userConverter.convertToDto(user)
    }
}
