@file:Suppress("RegExpUnexpectedAnchor")

package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.UnsupportedFieldException
import dev.gleroy.ivanachess.core.UserField
import dev.gleroy.ivanachess.core.UserService
import dev.gleroy.ivanachess.io.*
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
     * Get page of users.
     *
     * @param pageParams Page parameters.
     * @return Page.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getPage(@Valid pageParams: PageQueryParameters): PageRepresentation<UserRepresentation> {
        val pageOpts = pageConverter.convertToOptions(pageParams, UserField.values())
        return pageConverter.convertToRepresentation(userService.getPage(pageOpts)) { user ->
            userConverter.convertToRepresentation(user)
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
    ): PageRepresentation<UserRepresentation> {
        val supportedFields = UserField.values().filter { it.isSearchable }.toSet()
        val page = userService.search(
            term = searchParams.q!!,
            fields = searchParams.field
                .map { fieldLabel ->
                    supportedFields.find { it.label.equals(fieldLabel, true) } ?: throw UnsupportedFieldException(
                        fieldLabel = fieldLabel,
                        supportedFields = supportedFields,
                    )
                }
                .toSet(),
            pageOpts = pageConverter.convertToOptions(pageParams, UserField.values()),
            excluding = searchParams.exclude,
        )
        return pageConverter.convertToRepresentation(page) { userConverter.convertToRepresentation(it) }
    }

    /**
     * Create new user from subscription.
     *
     * @param representation User subscription.
     * @return Representation of user.
     */
    @PostMapping(ApiConstants.User.SignUpPath)
    @ResponseStatus(HttpStatus.CREATED)
    fun signUp(@RequestBody @Valid representation: UserSubscription): UserRepresentation {
        val user = userService.create(
            representation.pseudo,
            representation.email,
            passwordEncoder.encode(representation.password)
        )
        return userConverter.convertToRepresentation(user)
    }
}
