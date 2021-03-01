@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.user

import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.gleroy.ivanachess.api.AbstractControllerTest
import dev.gleroy.ivanachess.api.ApiConstants
import dev.gleroy.ivanachess.api.InvalidRequest
import dev.gleroy.ivanachess.api.security.Jwt
import dev.gleroy.ivanachess.dto.ErrorDto
import dev.gleroy.ivanachess.dto.LogInDto
import io.kotlintest.matchers.numerics.shouldBeZero
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.should
import io.kotlintest.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.request
import java.time.*
import javax.servlet.http.Cookie

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
internal class AuthenticationControllerTest : AbstractControllerTest() {
    private val now = Instant.now()

    @MockBean
    private lateinit var clock: Clock

    @Nested
    inner class logIn : WithBody() {
        override val method = HttpMethod.POST
        override val path = ApiConstants.Authentication.Path
        override val requestDto = LogInDto(
            pseudo = "user",
            password = "changeit"
        )
        override val invalidRequests = emptyList<InvalidRequest>()

        private lateinit var jwt: Jwt

        @BeforeEach
        fun beforeEach() {
            jwt = Jwt(
                pseudo = requestDto.pseudo,
                expirationDate = OffsetDateTime.ofInstant(
                    now.plusSeconds(props.auth.validity.toLong()),
                    ZoneOffset.systemDefault()
                ),
                token = "token"
            )
        }

        @Test
        fun `should return unauthorized if bad credentials`() {
            whenever(authService.generateJwt(requestDto.pseudo, requestDto.password))
                .thenThrow(BadCredentialsException(""))

            val responseBody = mvc.request(method, path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(requestDto)
            }
                .andDo { print() }
                .andExpect { status { isUnauthorized() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Unauthorized>(responseBody).shouldBeInstanceOf<ErrorDto.Unauthorized>()

            verify(authService).generateJwt(requestDto.pseudo, requestDto.password)
        }

        @Test
        fun `should generate JWT`() {
            whenever(authService.generateJwt(requestDto.pseudo, requestDto.password)).thenReturn(jwt)
            whenever(clock.instant()).thenReturn(now)

            val response = mvc.request(method, path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(requestDto)
            }
                .andDo { print() }
                .andExpect { status { isNoContent() } }
                .andReturn()
                .response
            response.contentLength.shouldBeZero()
            response.getHeaderValue(props.auth.header.name) shouldBe "${props.auth.header.valuePrefix}${jwt.token}"
            response.getCookie(props.auth.cookie.name) should { cookie ->
                cookie.shouldNotBeNull()
                cookie.domain shouldBe props.auth.cookie.domain
                cookie.secure shouldBe props.auth.cookie.secure
                cookie.isHttpOnly shouldBe props.auth.cookie.httpOnly
                cookie.maxAge shouldBe Duration.between(now, jwt.expirationDate).toSeconds().toInt()
                cookie.value shouldBe jwt.token
            }

            verify(authService).generateJwt(requestDto.pseudo, requestDto.password)
            verify(clock).instant()
        }
    }

    @Nested
    inner class logOut {
        @Test
        fun `should return unauthorized`() {
            val responseBody = mvc.request(HttpMethod.DELETE, ApiConstants.Authentication.Path)
                .andDo { print() }
                .andExpect { status { isUnauthorized() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Unauthorized>(responseBody).shouldBeInstanceOf<ErrorDto.Unauthorized>()
        }

        @Test
        fun `should delete cookie (with header auth)`() {
            shouldDeleteAuthenticationCookie { authenticationHeader(it) }
        }

        @Test
        fun `should delete cookie (with cookie auth)`() {
            shouldDeleteAuthenticationCookie { authenticationCookie(it) }
        }

        private fun shouldDeleteAuthenticationCookie(auth: MockHttpServletRequestDsl.(Jwt) -> Unit) =
            withAuthentication(simpleUser) { jwt ->
                val response = mvc.request(HttpMethod.DELETE, ApiConstants.Authentication.Path) { auth(jwt) }
                    .andDo { print() }
                    .andExpect { status { isNoContent() } }
                    .andReturn()
                    .response
                response.contentLength.shouldBeZero()
                response.getCookie(props.auth.cookie.name).shouldBeAuthenticationCookie("", 0)
            }
    }

    private fun Cookie?.shouldBeAuthenticationCookie(value: String, maxAge: Int) {
        shouldNotBeNull()
        domain shouldBe props.auth.cookie.domain
        secure shouldBe props.auth.cookie.secure
        isHttpOnly shouldBe props.auth.cookie.httpOnly
        this.maxAge shouldBe maxAge
        this.value shouldBe value
    }
}
