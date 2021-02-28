package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.gleroy.ivanachess.api.security.AuthenticationService
import dev.gleroy.ivanachess.api.security.Jwt
import dev.gleroy.ivanachess.api.security.UserDetailsAdapter
import dev.gleroy.ivanachess.dto.ErrorDto
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request
import java.time.OffsetDateTime
import javax.servlet.http.Cookie

internal abstract class AbstractControllerTest {
    protected val simpleUser = User(
        pseudo = "simple",
        bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
    )

    @MockBean
    protected lateinit var authService: AuthenticationService

    @Autowired
    protected lateinit var props: Properties

    @Autowired
    protected lateinit var mapper: ObjectMapper

    @Autowired
    protected lateinit var mvc: MockMvc

    abstract inner class Paginated {
        @Test
        fun `should return validation_error if page params are negative`() {
            val responseBody = mvc.request(method, path) {
                param(PageParam, "-1")
                param(SizeParam, "-1")
            }
                .andDo { print() }
                .andExpect { status { isBadRequest() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Validation>(responseBody) shouldBe ErrorDto.Validation(
                errors = setOf(
                    ErrorDto.InvalidParameter(
                        parameter = PageParam,
                        reason = "must be greater than or equal to 1"
                    ),
                    ErrorDto.InvalidParameter(
                        parameter = SizeParam,
                        reason = "must be greater than or equal to 1"
                    )
                )
            )
        }

        protected abstract val method: HttpMethod
        protected abstract val path: String

        private fun shouldReturnValidationErrorIfPageParametersAreInvalid() {

        }
    }

    abstract inner class WithBody {
        @Test
        fun `should return validation_error if request body is invalid`() =
            withAuthentication(simpleUser, invalidRequests.size) { jwt ->
                invalidRequests.forEach { req ->
                    val responseBody = mvc.request(method, path) {
                        authenticationHeader(jwt)
                        contentType = MediaType.APPLICATION_JSON
                        content = mapper.writeValueAsBytes(req.requestDto)
                    }
                        .andDo { print() }
                        .andExpect { status { isBadRequest() } }
                        .andReturn()
                        .response
                        .contentAsByteArray
                    mapper.readValue<ErrorDto.Validation>(responseBody) shouldBe req.responseDto
                }
            }

        protected abstract val method: HttpMethod
        protected abstract val path: String
        protected abstract val requestDto: Any
        protected abstract val invalidRequests: List<InvalidRequest>
    }

    protected fun withAuthentication(user: User, invocationsNb: Int = 1, block: (Jwt) -> Unit) {
        val jwt = Jwt(
            pseudo = user.pseudo,
            expirationDate = OffsetDateTime.now().plusDays(1),
            token = "token"
        )
        whenever(authService.parseJwt(jwt.token)).thenReturn(jwt)
        whenever(authService.loadUserByUsername(user.pseudo)).thenReturn(UserDetailsAdapter(user))
        block(jwt)
        verify(authService, times(invocationsNb)).parseJwt(jwt.token)
        verify(authService, times(invocationsNb)).loadUserByUsername(user.pseudo)
    }

    protected fun MockHttpServletRequestDsl.authenticationCookie(jwt: Jwt) {
        cookie(Cookie(props.auth.cookie, jwt.token))
    }

    protected fun MockHttpServletRequestDsl.authenticationHeader(jwt: Jwt) {
        header(props.auth.header, "${props.auth.headerValuePrefix}${jwt.token}")
    }
}
