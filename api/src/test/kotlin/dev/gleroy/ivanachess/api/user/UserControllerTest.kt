@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.user

import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.gleroy.ivanachess.api.AbstractControllerTest
import dev.gleroy.ivanachess.api.ApiConstants
import dev.gleroy.ivanachess.api.io.UserConverter
import dev.gleroy.ivanachess.dto.ErrorDto
import dev.gleroy.ivanachess.dto.UserDto
import dev.gleroy.ivanachess.dto.UserSubscriptionDto
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request
import java.time.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
internal class UserControllerTest : AbstractControllerTest() {
    private val user = User(
        pseudo = "admin",
        email = "admin@ivanachess.loc",
        creationDate = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC),
        bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
    )

    @MockBean
    private lateinit var passwordEncoder: BCryptPasswordEncoder

    @MockBean
    private lateinit var service: UserService

    @Autowired
    private lateinit var userConverter: UserConverter

    @Nested
    inner class search {
        private val q = "user"
        private val maxSize = 10
        private val users = listOf(user)
        private val userDtoList = users.map { userConverter.convertToDto(it) }

        @Test
        fun `should return validation_error if maxSize is negative`() {
            val responseBody = mvc.get("${ApiConstants.User.Path}${ApiConstants.SearchPath}") {
                param(ApiConstants.QueryParams.Q, q)
                param(ApiConstants.QueryParams.MaxSize, "-1")
            }
                .andDo { print() }
                .andExpect { status { isBadRequest() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Validation>(responseBody) shouldBe ErrorDto.Validation(
                errors = setOf(
                    tooLowNumberInvalidParameter(ApiConstants.QueryParams.MaxSize, 1)
                )
            )
        }

        @Test
        fun `should return validation_error if q is missing`() {
            val responseBody = mvc.get("${ApiConstants.User.Path}${ApiConstants.SearchPath}")
                .andDo { print() }
                .andExpect { status { isBadRequest() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Validation>(responseBody) shouldBe ErrorDto.Validation(
                errors = setOf(
                    missingParameterInvalidParameter(ApiConstants.QueryParams.Q)
                )
            )
        }

        @Test
        fun `should return users`() {
            whenever(service.searchByPseudo(q, maxSize)).thenReturn(users)

            val responseBody = mvc.get("${ApiConstants.User.Path}${ApiConstants.SearchPath}") {
                param(ApiConstants.QueryParams.Q, q)
                param(ApiConstants.QueryParams.MaxSize, maxSize.toString())
            }
                .andDo { print() }
                .andExpect { status { isOk() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<List<UserDto>>(responseBody) shouldBe userDtoList

            verify(service).searchByPseudo(q, maxSize)
        }
    }

    @Nested
    inner class signUp {
        private val bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
        private val subscriptionDto = UserSubscriptionDto(
            pseudo = "   ${user.pseudo}   ",
            email = " ${user.email}   ",
            password = "admin"
        )

        private val method = HttpMethod.POST
        private val path = "${ApiConstants.User.Path}/${ApiConstants.User.SignUpPath}"

        private lateinit var userDto: UserDto

        @BeforeEach
        fun beforeEach() {
            userDto = userConverter.convertToDto(user)
        }

        @Test
        fun `should return forbidden if user is authenticated`() = withAuthentication { jwt ->
            val responseBody = mvc.request(method, path) {
                authenticationHeader(jwt)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(subscriptionDto)
            }
                .andDo { print() }
                .andExpect { status { isForbidden() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Forbidden>(responseBody).shouldBeInstanceOf<ErrorDto.Forbidden>()
        }

        @Test
        fun `should return validation_error if strings are too small`() {
            val subscriptionDto = UserSubscriptionDto(
                pseudo = " ",
                email = " user@ivanachess.loc   ",
                password = ""
            )

            val responseBody = mvc.request(method, path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(subscriptionDto)
            }
                .andDo { print() }
                .andExpect { status { isBadRequest() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Validation>(responseBody) shouldBe ErrorDto.Validation(
                errors = setOf(
                    stringSizeInvalidParameter(
                        parameter = "pseudo",
                        min = UserSubscriptionDto.PseudoMinLength,
                        max = UserSubscriptionDto.PseudoMaxLength
                    ),
                    stringSizeInvalidParameter(
                        parameter = "password",
                        min = UserSubscriptionDto.PasswordMinLength,
                        max = UserSubscriptionDto.PasswordMaxLength
                    )
                )
            )
        }

        @Test
        fun `should return validation_error if strings are too big`() {
            val subscriptionDto = UserSubscriptionDto(
                pseudo = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                email = " user@ivanachess.loc   ",
                password = "changeit"
            )

            val responseBody = mvc.request(method, path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(subscriptionDto)
            }
                .andDo { print() }
                .andExpect { status { isBadRequest() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Validation>(responseBody) shouldBe ErrorDto.Validation(
                errors = setOf(
                    stringSizeInvalidParameter(
                        parameter = "pseudo",
                        min = UserSubscriptionDto.PseudoMinLength,
                        max = UserSubscriptionDto.PseudoMaxLength
                    )
                )
            )
        }

        @Test
        fun `should return validation_error if email is invalid`() {
            val subscriptionDto = UserSubscriptionDto(
                pseudo = "admin",
                email = "email",
                password = "changeit"
            )

            val responseBody = mvc.request(method, path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(subscriptionDto)
            }
                .andDo { print() }
                .andExpect { status { isBadRequest() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Validation>(responseBody) shouldBe ErrorDto.Validation(
                errors = setOf(
                    ErrorDto.InvalidParameter(
                        parameter = "email",
                        reason = "must be a well-formed email address"
                    )
                )
            )
        }

        @Test
        fun `should return validation_error if strings do not match pattern`() {
            val subscriptionDto = UserSubscriptionDto(
                pseudo = "user_é",
                email = "user@ivanachess.loc",
                password = "changeit"
            )

            val responseBody = mvc.request(method, path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(subscriptionDto)
            }
                .andDo { print() }
                .andExpect { status { isBadRequest() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Validation>(responseBody) shouldBe ErrorDto.Validation(
                errors = setOf(
                    ErrorDto.InvalidParameter(
                        parameter = "pseudo",
                        reason = "must match \"${UserSubscriptionDto.PseudoRegex}\""
                    )
                )
            )
        }

        @Test
        fun `should return pseudo_already_used if pseudo is already used`() {
            whenever(passwordEncoder.encode(subscriptionDto.password)).thenReturn(bcryptPassword)
            whenever(service.create(user.pseudo, user.email, bcryptPassword))
                .thenThrow(UserPseudoAlreadyUsedException(user.pseudo))

            val responseBody = mvc.request(method, path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(subscriptionDto)
            }
                .andDo { print() }
                .andExpect { status { isConflict() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.UserPseudoAlreadyUsed>(responseBody) shouldBe ErrorDto.UserPseudoAlreadyUsed(
                pseudo = user.pseudo
            )

            verify(passwordEncoder).encode(subscriptionDto.password)
            verify(service).create(user.pseudo, user.email, bcryptPassword)
        }

        @Test
        fun `should return email_already_used if email is already used`() {
            whenever(passwordEncoder.encode(subscriptionDto.password)).thenReturn(bcryptPassword)
            whenever(service.create(user.pseudo, user.email, bcryptPassword))
                .thenThrow(UserEmailAlreadyUsedException(user.email))

            val responseBody = mvc.request(method, path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(subscriptionDto)
            }
                .andDo { print() }
                .andExpect { status { isConflict() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.UserEmailAlreadyUsed>(responseBody) shouldBe ErrorDto.UserEmailAlreadyUsed(
                email = user.email
            )

            verify(passwordEncoder).encode(subscriptionDto.password)
            verify(service).create(user.pseudo, user.email, bcryptPassword)
        }

        @Test
        fun `should return create user`() {
            whenever(passwordEncoder.encode(subscriptionDto.password)).thenReturn(bcryptPassword)
            whenever(service.create(user.pseudo, user.email, bcryptPassword)).thenReturn(user)

            val responseBody = mvc.request(method, path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(subscriptionDto)
            }
                .andDo { print() }
                .andExpect { status { isCreated() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<UserDto>(responseBody) shouldBe userDto

            verify(passwordEncoder).encode(subscriptionDto.password)
            verify(service).create(user.pseudo, user.email, bcryptPassword)
        }
    }
}
