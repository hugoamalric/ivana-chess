@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.user

import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.gleroy.ivanachess.api.*
import dev.gleroy.ivanachess.dto.ErrorDto
import dev.gleroy.ivanachess.dto.ExistsDto
import dev.gleroy.ivanachess.dto.UserSubscriptionDto
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
            shouldReturnValidationErrorDto(
                expectedResponseBody = ErrorDto.Validation(
                    errors = setOf(
                        createMissingParameterDto(ApiConstants.QueryParams.By),
                    )
                )
            )
        }

        @Test
        fun `should return validation_error if value parameter is missing`() {
            shouldReturnValidationErrorDto(
                params = mapOf(
                    ApiConstants.QueryParams.By to listOf(UserSearchableField.Pseudo.label),
                ),
                expectedResponseBody = ErrorDto.Validation(
                    errors = setOf(
                        createMissingParameterDto(ApiConstants.QueryParams.Value),
                    )
                )
            )
        }

        @Test
        fun `should return validation_error if field is unsupported`() {
            shouldReturnValidationErrorDto(
                params = mapOf(
                    ApiConstants.QueryParams.By to listOf("password"),
                    ApiConstants.QueryParams.Value to listOf(value),
                ),
                expectedResponseBody = ErrorDto.Validation(
                    errors = setOf(
                        ErrorDto.UnsupportedField(setOf("pseudo", "email"))
                    )
                )
            )
        }

        @Test
        fun `should check if user exists by email`() {
            shouldCheckIfUserExists(
                fieldLabel = UserSearchableField.Email.label,
                mock = { whenever(userService.existsWithEmail(value)).thenReturn(true) },
                verify = { verify(userService).existsWithEmail(value) }
            )
        }

        @Test
        fun `should check if user exists by pseudo`() {
            shouldCheckIfUserExists(
                fieldLabel = UserSearchableField.Pseudo.label,
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
                expectedResponseBody = ExistsDto(true),
            ) { mapper.readValue(it) }
            verify()
        }
    }

    @Nested
    inner class search : PaginatedEndpointTest() {
        override val method = HttpMethod.GET
        override val path = "${ApiConstants.User.Path}${ApiConstants.SearchPath}"

        private val term = "user"
        private val fields = UserSearchableField.values().toSet()
        private val pageOpts = PageOptions(
            number = 1,
            size = 10,
            sorts = listOf(
                EntitySort(UserSortableField.Pseudo),
                EntitySort(UserSortableField.Email, EntitySort.Order.Descending),
                EntitySort(CommonSortableEntityField.Id, EntitySort.Order.Descending),
                EntitySort(CommonSortableEntityField.CreationDate),
            ),
        )
        private val page = Page(
            content = listOf(simpleUser),
            number = pageOpts.number,
            totalPages = 1,
            totalItems = 1,
        )

        override fun `should return validation_error if page parameters are negative`() {
            shouldReturnValidationErrorDtoIfPageParametersAreInvalid(
                value = -1,
                params = mapOf(ApiConstants.QueryParams.Q to listOf(term)),
            )
        }

        override fun `should return validation_error if page parameters are 0`() {
            shouldReturnValidationErrorDtoIfPageParametersAreInvalid(
                value = 0,
                params = mapOf(ApiConstants.QueryParams.Q to listOf(term)),
            )
        }

        @Test
        fun `should return validation_error if search parameters are invalid`() {
            shouldReturnValidationErrorDto(
                expectedResponseBody = ErrorDto.Validation(
                    errors = setOf(
                        createMissingParameterDto(ApiConstants.QueryParams.Q),
                        createEmptyParameterDto(ApiConstants.QueryParams.Field),
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
                expectedResponseBody = pageConverter.convertToDto(page) { userConverter.convertToDto(it) },
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
        private val dto = UserSubscriptionDto(
            pseudo = "  ${simpleUser.pseudo}   ",
            email = " ${simpleUser.email}   ",
            password = "changeit"
        )

        override val method = HttpMethod.POST
        override val path = "${ApiConstants.User.Path}/${ApiConstants.User.SignUpPath}"

        @Test
        fun `should return forbidden if user is authenticated`() = withAuthentication { jwt ->
            doRequest(
                cookies = listOf(createAuthenticationCookie(jwt)),
                expectedStatus = HttpStatus.FORBIDDEN,
                expectedResponseBody = ErrorDto.Forbidden,
            ) { mapper.readValue(it) }
        }

        @Test
        fun `should return validation_error if strings are too small`() {
            shouldReturnValidationErrorDto(
                body = dto.copy(
                    pseudo = buildString(1),
                    password = ""
                ),
                expectedResponseBody = ErrorDto.Validation(
                    errors = setOf(
                        createInvalidSizeParameterDto(
                            parameter = "pseudo",
                            min = UserSubscriptionDto.PseudoMinLength,
                            max = UserSubscriptionDto.PseudoMaxLength
                        ),
                        createInvalidSizeParameterDto(
                            parameter = "password",
                            min = UserSubscriptionDto.PasswordMinLength,
                            max = UserSubscriptionDto.PasswordMaxLength
                        ),
                    )
                ),
            )
        }

        @Test
        fun `should return validation_error if strings are too long`() {
            shouldReturnValidationErrorDto(
                body = dto.copy(
                    pseudo = buildString(51),
                ),
                expectedResponseBody = ErrorDto.Validation(
                    errors = setOf(
                        createInvalidSizeParameterDto(
                            parameter = "pseudo",
                            min = UserSubscriptionDto.PseudoMinLength,
                            max = UserSubscriptionDto.PseudoMaxLength
                        ),
                    )
                ),
            )
        }

        @Test
        fun `should return validation_error if email is invalid`() {
            shouldReturnValidationErrorDto(
                body = dto.copy(
                    email = "email",
                ),
                expectedResponseBody = ErrorDto.Validation(
                    errors = setOf(
                        createInvalidEmailParameterDto("email"),
                    )
                ),
            )
        }

        @Test
        fun `should return validation_error if strings do not match pattern`() {
            shouldReturnValidationErrorDto(
                body = dto.copy(
                    pseudo = "user_é",
                ),
                expectedResponseBody = ErrorDto.Validation(
                    errors = setOf(
                        createMalformedParameterDto("pseudo", UserSubscriptionDto.PseudoRegex),
                    )
                ),
            )
        }

        @Test
        fun `should return pseudo_already_used if pseudo is already used`() {
            whenever(passwordEncoder.encode(dto.password)).thenReturn(bcryptPassword)
            whenever(userService.create(simpleUser.pseudo, simpleUser.email, bcryptPassword))
                .thenThrow(UserPseudoAlreadyUsedException(simpleUser.pseudo))

            doRequest(
                body = dto,
                expectedStatus = HttpStatus.CONFLICT,
                expectedResponseBody = ErrorDto.UserPseudoAlreadyUsed(
                    pseudo = simpleUser.pseudo
                ),
            ) { mapper.readValue(it) }

            verify(passwordEncoder).encode(dto.password)
            verify(userService).create(simpleUser.pseudo, simpleUser.email, bcryptPassword)
        }

        @Test
        fun `should return email_already_used if email is already used`() {
            whenever(passwordEncoder.encode(dto.password)).thenReturn(bcryptPassword)
            whenever(userService.create(simpleUser.pseudo, simpleUser.email, bcryptPassword))
                .thenThrow(UserEmailAlreadyUsedException(simpleUser.email))

            doRequest(
                body = dto,
                expectedStatus = HttpStatus.CONFLICT,
                expectedResponseBody = ErrorDto.UserEmailAlreadyUsed(
                    email = simpleUser.email
                ),
            ) { mapper.readValue(it) }

            verify(passwordEncoder).encode(dto.password)
            verify(userService).create(simpleUser.pseudo, simpleUser.email, bcryptPassword)
        }

        @Test
        fun `should return create user`() {
            whenever(passwordEncoder.encode(dto.password)).thenReturn(bcryptPassword)
            whenever(userService.create(simpleUser.pseudo, simpleUser.email, bcryptPassword)).thenReturn(simpleUser)

            doRequest(
                body = dto,
                expectedStatus = HttpStatus.CREATED,
                expectedResponseBody = userConverter.convertToDto(simpleUser),
            ) { mapper.readValue(it) }

            verify(passwordEncoder).encode(dto.password)
            verify(userService).create(simpleUser.pseudo, simpleUser.email, bcryptPassword)
        }
    }
}
