@file:Suppress("RegExpUnexpectedAnchor")

package dev.gleroy.ivanachess.api.user

import dev.gleroy.ivanachess.api.ApiConstants
import dev.gleroy.ivanachess.api.UnsupportedFieldException
import dev.gleroy.ivanachess.api.io.PageConverter
import dev.gleroy.ivanachess.api.io.PageQueryParameters
import dev.gleroy.ivanachess.api.io.SearchQueryParameters
import dev.gleroy.ivanachess.api.io.UserConverter
import dev.gleroy.ivanachess.dto.ExistsDto
import dev.gleroy.ivanachess.dto.PageDto
import dev.gleroy.ivanachess.dto.UserDto
import dev.gleroy.ivanachess.dto.UserSubscriptionDto
import org.slf4j.LoggerFactory
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
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(UserController::class.java)
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
        val lowerCaseBy = by.toLowerCase()
        val field = UserSearchableField.values().find { it.label == lowerCaseBy }
            ?: throw UnsupportedFieldException(lowerCaseBy, UserSearchableField.values().toSet()).apply {
                Logger.debug(message)
            }
        return when (field) {
            UserSearchableField.Email -> ExistsDto(userService.existsWithEmail(value))
            UserSearchableField.Pseudo -> ExistsDto(userService.existsWithPseudo(value))
        }
    }

    /**
     * Search user.
     *
     * @param searchParams Search parameters.
     * @param pageParams Page parameters.
     * @return Page.
     */
    @GetMapping(ApiConstants.SearchPath)
    @ResponseStatus(HttpStatus.OK)
    fun search(
        @Valid searchParams: SearchQueryParameters,
        @Valid pageParams: PageQueryParameters,
    ): PageDto<UserDto> {
        val fields = UserSearchableField.values().toSet()
        val page = userService.search(
            term = searchParams.q!!,
            fields = searchParams.field
                .map { fieldLabel ->
                    fields.find { it.label.equals(fieldLabel, true) } ?: throw UnsupportedFieldException(
                        fieldLabel = fieldLabel,
                        supportedFields = fields,
                    )
                }
                .toSet(),
            pageOpts = pageConverter.convertToOptions(pageParams, UserSortableField.values().toSet()),
            excluding = searchParams.exclude,
        )
        return pageConverter.convertToDto(page) { userConverter.convertToDto(it) }
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
