@file:Suppress("RegExpUnexpectedAnchor")

package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.*
import dev.gleroy.ivanachess.io.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

/**
 * User API controller.
 *
 * @param userService User service.
 * @param userConverter User converter.
 * @param pageConverter Page converter.
 * @param props Properties.
 */
@RestController
@RequestMapping(ApiConstants.User.Path)
@Validated
class UserController(
    private val userService: UserService,
    private val userConverter: UserConverter,
    private val pageConverter: PageConverter,
    private val passwordEncoder: BCryptPasswordEncoder,
    override val props: Properties,
) : AbstractController() {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(UserController::class.java)
    }

    /**
     * Delete user.
     *
     * @param id User ID.
     * @param auth Authentication.
     */
    @DeleteMapping("/{id:${ApiConstants.UuidRegex}}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID, auth: Authentication, response: HttpServletResponse) {
        val user = authenticatedUser(auth)
        if (user.role < User.Role.SuperAdmin && user.id != id) {
            throw NotAllowedException("User '${user.pseudo}' (${user.id}) attempted to delete user $id").apply {
                Logger.warn(message)
            }
        }
        userService.delete(id)
        if (user.id == id) {
            response.addCookie(expireAuthenticationCookie())
        }
    }

    /**
     * Get user by its ID.
     *
     * @param id User ID.
     * @return Representation of user.
     */
    @GetMapping("/{id:${ApiConstants.UuidRegex}}")
    @ResponseStatus(HttpStatus.OK)
    fun get(@PathVariable id: UUID): UserRepresentation {
        val user = userService.getById(id)
        return userConverter.convertToPublicRepresentation(user)
    }

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
            userConverter.convertToPublicRepresentation(user)
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
        return pageConverter.convertToRepresentation(page) { userConverter.convertToPublicRepresentation(it) }
    }

    /**
     * User update by user itself.
     *
     * @param update User update.
     * @param auth Authentication.
     * @return Representation of user.
     */
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    fun selfUpdate(@RequestBody @Valid update: UserUpdate.Self, auth: Authentication): UserRepresentation {
        val user = userService.update(
            id = authenticatedUser(auth).id,
            email = update.email,
            bcryptPassword = update.password?.let { passwordEncoder.encode(it) },
        )
        return userConverter.convertToPublicRepresentation(user)
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
        return userConverter.convertToPublicRepresentation(user)
    }
}
