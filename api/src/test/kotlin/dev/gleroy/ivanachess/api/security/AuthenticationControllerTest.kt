@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.security

import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.gleroy.ivanachess.api.AbstractControllerTest
import dev.gleroy.ivanachess.api.ApiConstants
import dev.gleroy.ivanachess.io.Credentials
import dev.gleroy.ivanachess.io.ErrorRepresentation
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.should
import io.kotlintest.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.servlet.http.Cookie

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
internal class AuthenticationControllerTest : AbstractControllerTest() {
    private val now = Instant.now()

    @Nested
    inner class logIn : EndpointTest() {
        private val creds = Credentials(
            pseudo = "user",
            password = "changeit"
        )

        override val method = HttpMethod.POST
        override val path = ApiConstants.Authentication.Path

        private lateinit var jwt: Jwt

        @BeforeEach
        fun beforeEach() {
            jwt = Jwt(
                pseudo = creds.pseudo,
                expirationDate = OffsetDateTime.ofInstant(
                    now.plusSeconds(props.auth.validity.toLong()),
                    ZoneOffset.systemDefault()
                ),
                token = "token"
            )
        }

        @Test
        fun `should return bad_credentials if bad credentials`() {
            whenever(authService.generateJwt(creds.pseudo, creds.password)).thenThrow(BadCredentialsException(""))

            doRequest(
                body = creds,
                expectedStatus = HttpStatus.UNAUTHORIZED,
                expectedResponseBody = ErrorRepresentation.BadCredentials,
            ) { mapper.readValue(it) }

            verify(authService).generateJwt(creds.pseudo, creds.password)
        }

        @Test
        fun `should generate JWT`() {
            whenever(authService.generateJwt(creds.pseudo, creds.password)).thenReturn(jwt)
            whenever(clock.instant()).thenReturn(now)

            val response = doRequest(
                body = creds,
                expectedStatus = HttpStatus.NO_CONTENT,
            )
            response.getHeaderValue(props.auth.header.name) shouldBe "${props.auth.header.valuePrefix}${jwt.token}"
            response.getCookie(props.auth.cookie.name) should { cookie ->
                cookie.shouldNotBeNull()
                cookie.shouldBeAuthenticationCookie(
                    value = jwt.token,
                    maxAge = Duration.between(now, jwt.expirationDate).toSeconds().toInt()
                )
            }

            verify(authService).generateJwt(creds.pseudo, creds.password)
            verify(clock).instant()
        }
    }

    @Nested
    inner class logOut : EndpointTest() {
        override val method = HttpMethod.DELETE
        override val path = ApiConstants.Authentication.Path

        @Test
        fun `should return unauthorized`() {
            shouldReturnUnauthorized()
        }

        @Test
        fun `should delete cookie`() = withAuthentication { jwt ->
            val response = doRequest(
                cookies = listOf(createAuthenticationCookie(jwt)),
                expectedStatus = HttpStatus.NO_CONTENT,
            )
            response.getCookie(props.auth.cookie.name).shouldBeAuthenticationCookie("", 0)
        }
    }

    @Nested
    inner class me : EndpointTest() {
        override val method = HttpMethod.GET
        override val path = ApiConstants.Authentication.Path

        @Test
        fun `should return unauthorized`() {
            shouldReturnUnauthorized()
        }

        @Test
        fun `should return authenticated user`() = withAuthentication { jwt ->
            doRequest(
                cookies = listOf(createAuthenticationCookie(jwt)),
                expectedResponseBody = userConverter.convertToRepresentation(simpleUser),
            ) { mapper.readValue(it) }
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
