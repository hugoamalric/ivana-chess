@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.gleroy.ivanachess.dto.ErrorDto
import dev.gleroy.ivanachess.dto.UserDto
import dev.gleroy.ivanachess.dto.UserSubscriptionDto
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
import org.springframework.test.web.servlet.request
import java.time.ZoneOffset

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
internal class UserControllerTest : AbstractControllerTest() {
    private val user = User(
        pseudo = "admin",
        bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
    )

    @MockBean
    private lateinit var passwordEncoder: BCryptPasswordEncoder

    @MockBean
    private lateinit var service: UserService

    @Autowired
    private lateinit var pageConverter: PageConverter

    @Autowired
    private lateinit var userConverter: UserConverter

    @Nested
    inner class signUp : AbstractControllerTest.WithBody() {
        private val bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"

        override val method = HttpMethod.POST
        override val path = "${ApiConstants.User.Path}/${ApiConstants.User.SignUpPath}"
        override val requestDto = UserSubscriptionDto(
            pseudo = "user",
            password = "admin"
        )
        override val invalidRequests = listOf(
            InvalidRequest(
                requestDto = UserSubscriptionDto(
                    pseudo = " ",
                    password = ""
                ),
                responseDto = ErrorDto.Validation(
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
                        ),
                    )
                )
            ),
            InvalidRequest(
                requestDto = UserSubscriptionDto(
                    pseudo = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                    password = "changeit"
                ),
                responseDto = ErrorDto.Validation(
                    errors = setOf(
                        stringSizeInvalidParameter(
                            parameter = "pseudo",
                            min = UserSubscriptionDto.PseudoMinLength,
                            max = UserSubscriptionDto.PseudoMaxLength
                        )
                    )
                )
            )
        )

        private lateinit var userDto: UserDto

        @BeforeEach
        fun beforeEach() {
            userDto = userConverter.convert(user)
        }

        @Test
        fun `should return pseudo_already_used if pseudo is already used`() {
            whenever(passwordEncoder.encode(requestDto.password)).thenReturn(bcryptPassword)
            whenever(service.create(requestDto.pseudo, bcryptPassword, User.Role.Simple))
                .thenThrow(UserPseudoAlreadyUsedException(requestDto.pseudo))

            val responseBody = mvc.request(method, path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(requestDto)
            }
                .andDo { print() }
                .andExpect { status { isConflict() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.PseudoAlreadyUsed>(responseBody) shouldBe ErrorDto.PseudoAlreadyUsed(
                pseudo = requestDto.pseudo
            )

            verify(passwordEncoder).encode(requestDto.password)
            verify(service).create(requestDto.pseudo, bcryptPassword, User.Role.Simple)
        }

        @Test
        fun `should return create user`() {
            whenever(passwordEncoder.encode(requestDto.password)).thenReturn(bcryptPassword)
            whenever(service.create(requestDto.pseudo, bcryptPassword, User.Role.Simple)).thenReturn(user)

            val responseBody = mvc.request(method, path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(requestDto)
            }
                .andDo { print() }
                .andExpect { status { isCreated() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<UserDto>(responseBody) shouldBe userDto.atUtc()

            verify(passwordEncoder).encode(requestDto.password)
            verify(service).create(requestDto.pseudo, bcryptPassword, User.Role.Simple)
        }
    }

    private fun UserDto.atUtc() = copy(creationDate = creationDate.withOffsetSameInstant(ZoneOffset.UTC))
}
