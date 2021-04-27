@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.gleroy.ivanachess.core.*
import dev.gleroy.ivanachess.io.ApiConstants
import dev.gleroy.ivanachess.io.ErrorRepresentation
import dev.gleroy.ivanachess.io.ExistsRepresentation
import dev.gleroy.ivanachess.io.UserSubscription
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
internal class UserControllerTest : AbstractControllerTest() {
    @Nested
    inner class exists : EndpointTest() {
        private val value = "user"

        override val method = HttpMethod.GET
        override val path = "${ApiConstants.User.Path}${ApiConstants.ExistsPath}"

        @Test
        fun `should return validation_error if by parameter is missing`() {
            shouldReturnValidationErrorRepresentation(
                expectedResponseBody = ErrorRepresentation.Validation(
                    errors = setOf(
                        createMissingParameterErrorRepresentation(ApiConstants.QueryParams.By),
                    )
                )
            )
        }

        @Test
        fun `should return validation_error if value parameter is missing`() {
            shouldReturnValidationErrorRepresentation(
                params = mapOf(
                    ApiConstants.QueryParams.By to listOf(UserField.Pseudo.label),
                ),
                expectedResponseBody = ErrorRepresentation.Validation(
                    errors = setOf(
                        createMissingParameterErrorRepresentation(ApiConstants.QueryParams.Value),
                    )
                )
            )
        }

        @Test
        fun `should return validation_error if field is unsupported`() {
            shouldReturnValidationErrorRepresentation(
                params = mapOf(
                    ApiConstants.QueryParams.By to listOf("password"),
                    ApiConstants.QueryParams.Value to listOf(value),
                ),
                expectedResponseBody = ErrorRepresentation.Validation(
                    errors = setOf(
                        ErrorRepresentation.UnsupportedField(setOf("pseudo", "email"))
                    )
                )
            )
        }

        @Test
        fun `should check if user exists by email`() {
            shouldCheckIfUserExists(
                fieldLabel = UserField.Email.label,
                mock = { whenever(userService.existsWithEmail(value)).thenReturn(true) },
                verify = { verify(userService).existsWithEmail(value) }
            )
        }

        @Test
        fun `should check if user exists by pseudo`() {
            shouldCheckIfUserExists(
                fieldLabel = UserField.Pseudo.label,
                mock = { whenever(userService.existsWithPseudo(value)).thenReturn(true) },
                verify = { verify(userService).existsWithPseudo(value) }
            )
        }

        private fun shouldCheckIfUserExists(fieldLabel: String, mock: () -> Unit, verify: () -> Unit) {
            mock()
            doRequest(
                params = mapOf(
                    ApiConstants.QueryParams.By to listOf(fieldLabel),
                    ApiConstants.QueryParams.Value to listOf(value),
                ),
                expectedResponseBody = ExistsRepresentation(true),
            ) { mapper.readValue(it) }
            verify()
        }
    }

    @Nested
    inner class getPage : PaginatedEndpointTest() {
        override val method = HttpMethod.GET
        override val path = ApiConstants.User.Path

        private val pageOpts = PageOptions(
            number = 1,
            size = 10,
            sorts = listOf(
                ItemSort(CommonEntityField.Id, ItemSort.Order.Descending),
                ItemSort(CommonEntityField.CreationDate),
            ),
            filters = setOf(
                ItemFilter(UserField.Pseudo, "user1"),
            )
        )
        private val page = Page(
            content = listOf(simpleUser),
            number = pageOpts.number,
            totalPages = 1,
            totalItems = 1,
        )

        @Test
        fun `should return page`() {
            whenever(userService.getPage(pageOpts)).thenReturn(page)

            doRequest(
                pageOpts = pageOpts,
                expectedResponseBody = pageConverter.convertToRepresentation(page) { user ->
                    userConverter.convertToRepresentation(user)
                },
            ) { mapper.readValue(it) }

            verify(userService).getPage(pageOpts)
        }
    }

    @Nested
    inner class search : PaginatedEndpointTest() {
        override val method = HttpMethod.GET
        override val path = "${ApiConstants.User.Path}${ApiConstants.SearchPath}"

        private val term = "user"
        private val fields = UserField.values().filter { it.isSearchable }.toSet()
        private val pageOpts = PageOptions(
            number = 1,
            size = 10,
            sorts = listOf(
                ItemSort(UserField.Pseudo),
                ItemSort(UserField.Email, ItemSort.Order.Descending),
                ItemSort(CommonEntityField.Id, ItemSort.Order.Descending),
                ItemSort(CommonEntityField.CreationDate),
            ),
        )
        private val page = Page(
            content = listOf(simpleUser),
            number = pageOpts.number,
            totalPages = 1,
            totalItems = 1,
        )

        override fun `should return validation_error if page parameters are negative`() {
            shouldReturnValidationErrorRepresentationIfPageParametersAreInvalid(
                value = -1,
                params = mapOf(ApiConstants.QueryParams.Q to listOf(term)),
            )
        }

        override fun `should return validation_error if page parameters are 0`() {
            shouldReturnValidationErrorRepresentationIfPageParametersAreInvalid(
                value = 0,
                params = mapOf(ApiConstants.QueryParams.Q to listOf(term)),
            )
        }

        @Test
        fun `should return validation_error if search parameters are invalid`() {
            shouldReturnValidationErrorRepresentation(
                expectedResponseBody = ErrorRepresentation.Validation(
                    errors = setOf(
                        createMissingParameterErrorRepresentation(ApiConstants.QueryParams.Q),
                        createEmptyParameterErrorRepresentation(ApiConstants.QueryParams.Field),
                    )
                )
            )
        }

        @Test
        fun `should return page`() {
            shouldReturnPage()
        }

        @Test
        fun `should return page excluding some users`() {
            shouldReturnPage(setOf(UUID.randomUUID(), UUID.randomUUID()))
        }

        private fun shouldReturnPage(excluding: Set<UUID> = emptySet()) {
            val excludingParams = if (excluding.isEmpty()) {
                emptyMap()
            } else {
                mapOf(ApiConstants.QueryParams.Exclude to excluding.map { it.toString() })
            }

            whenever(
                userService.search(
                    term = term,
                    fields = fields,
                    pageOpts = pageOpts,
                    excluding = excluding,
                )
            ).thenReturn(page)

            doRequest(
                pageOpts = pageOpts,
                params = excludingParams + mapOf(
                    ApiConstants.QueryParams.Q to listOf(term),
                    ApiConstants.QueryParams.Field to fields.map { it.label },
                ),
                expectedResponseBody = pageConverter.convertToRepresentation(page) { user ->
                    userConverter.convertToRepresentation(user)
                },
            ) { mapper.readValue(it) }

            verify(userService).search(
                term = term,
                fields = fields,
                pageOpts = pageOpts,
                excluding = excluding,
            )
        }
    }

    @Nested
    inner class signUp : EndpointTest() {
        private val bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
        private val userSubscription = UserSubscription(
            pseudo = "  ${simpleUser.pseudo}   ",
            email = " ${simpleUser.email}   ",
            password = "changeit"
        )

        override val method = HttpMethod.POST
        override val path = "${ApiConstants.User.Path}/${ApiConstants.User.SignUpPath}"

        @Test
        fun `should return forbidden if user is authenticated`() = withAuthentication { jwt ->
            shouldReturnForbiddenErrorRepresentation(
                cookies = listOf(createAuthenticationCookie(jwt)),
            )
        }

        @Test
        fun `should return validation_error if strings are too small`() {
            shouldReturnValidationErrorRepresentation(
                body = userSubscription.copy(
                    pseudo = buildString(1),
                    password = ""
                ),
                expectedResponseBody = ErrorRepresentation.Validation(
                    errors = setOf(
                        createInvalidSizeParameterErrorRepresentation(
                            parameter = "pseudo",
                            min = ApiConstants.Constraints.MinPseudoLength,
                            max = ApiConstants.Constraints.MaxPseudoLength,
                        ),
                        createInvalidSizeParameterErrorRepresentation(
                            parameter = "password",
                            min = ApiConstants.Constraints.MinPasswordLength,
                        ),
                    )
                ),
            )
        }

        @Test
        fun `should return validation_error if strings are too long`() {
            shouldReturnValidationErrorRepresentation(
                body = userSubscription.copy(
                    pseudo = buildString(51),
                ),
                expectedResponseBody = ErrorRepresentation.Validation(
                    errors = setOf(
                        createInvalidSizeParameterErrorRepresentation(
                            parameter = "pseudo",
                            min = ApiConstants.Constraints.MinPseudoLength,
                            max = ApiConstants.Constraints.MaxPseudoLength,
                        ),
                    )
                ),
            )
        }

        @Test
        fun `should return validation_error if email is invalid`() {
            shouldReturnValidationErrorRepresentation(
                body = userSubscription.copy(
                    email = "email",
                ),
                expectedResponseBody = ErrorRepresentation.Validation(
                    errors = setOf(
                        createInvalidEmailParameterErrorRepresentation("email"),
                    )
                ),
            )
        }

        @Test
        fun `should return validation_error if strings do not match pattern`() {
            shouldReturnValidationErrorRepresentation(
                body = userSubscription.copy(
                    pseudo = "user_é",
                ),
                expectedResponseBody = ErrorRepresentation.Validation(
                    errors = setOf(
                        createMalformedParameterErrorRepresentation("pseudo", ApiConstants.Constraints.PseudoRegex),
                    )
                ),
            )
        }

        @Test
        fun `should return pseudo_already_used if pseudo is already used`() {
            whenever(passwordEncoder.encode(userSubscription.password)).thenReturn(bcryptPassword)
            whenever(userService.create(simpleUser.pseudo, simpleUser.email, bcryptPassword))
                .thenThrow(UserPseudoAlreadyUsedException(simpleUser.pseudo))

            doRequest(
                body = userSubscription,
                expectedStatus = HttpStatus.CONFLICT,
                expectedResponseBody = ErrorRepresentation.UserPseudoAlreadyUsed(
                    pseudo = simpleUser.pseudo
                ),
            ) { mapper.readValue(it) }

            verify(passwordEncoder).encode(userSubscription.password)
            verify(userService).create(simpleUser.pseudo, simpleUser.email, bcryptPassword)
        }

        @Test
        fun `should return email_already_used if email is already used`() {
            whenever(passwordEncoder.encode(userSubscription.password)).thenReturn(bcryptPassword)
            whenever(userService.create(simpleUser.pseudo, simpleUser.email, bcryptPassword))
                .thenThrow(UserEmailAlreadyUsedException(simpleUser.email))

            doRequest(
                body = userSubscription,
                expectedStatus = HttpStatus.CONFLICT,
                expectedResponseBody = ErrorRepresentation.UserEmailAlreadyUsed(
                    email = simpleUser.email
                ),
            ) { mapper.readValue(it) }

            verify(passwordEncoder).encode(userSubscription.password)
            verify(userService).create(simpleUser.pseudo, simpleUser.email, bcryptPassword)
        }

        @Test
        fun `should return create user`() {
            whenever(passwordEncoder.encode(userSubscription.password)).thenReturn(bcryptPassword)
            whenever(userService.create(simpleUser.pseudo, simpleUser.email, bcryptPassword)).thenReturn(simpleUser)

            doRequest(
                body = userSubscription,
                expectedStatus = HttpStatus.CREATED,
                expectedResponseBody = userConverter.convertToRepresentation(simpleUser),
            ) { mapper.readValue(it) }

            verify(passwordEncoder).encode(userSubscription.password)
            verify(userService).create(simpleUser.pseudo, simpleUser.email, bcryptPassword)
        }
    }
}
