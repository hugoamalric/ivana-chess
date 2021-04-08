package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.atLeast
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.gleroy.ivanachess.api.io.PageConverter
import dev.gleroy.ivanachess.api.io.UserConverter
import dev.gleroy.ivanachess.api.security.AuthenticationService
import dev.gleroy.ivanachess.api.security.Jwt
import dev.gleroy.ivanachess.api.security.UserDetailsAdapter
import dev.gleroy.ivanachess.api.user.User
import dev.gleroy.ivanachess.api.user.UserService
import dev.gleroy.ivanachess.dto.ErrorDto
import dev.gleroy.ivanachess.dto.PageDto
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.servlet.http.Cookie

internal abstract class AbstractControllerTest {
    protected val simpleUser = User(
        pseudo = "simple",
        email = "simple@ivanachess.loc",
        creationDate = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC),
        bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
    )

    @MockBean
    protected lateinit var authService: AuthenticationService

    @MockBean
    protected lateinit var userService: UserService

    @MockBean
    protected lateinit var passwordEncoder: BCryptPasswordEncoder

    @Autowired
    protected lateinit var userConverter: UserConverter

    @Autowired
    protected lateinit var pageConverter: PageConverter

    @Autowired
    protected lateinit var props: Properties

    @Autowired
    protected lateinit var mapper: ObjectMapper

    @Autowired
    protected lateinit var mvc: MockMvc

    abstract inner class EndpointTest {
        protected val simpleUser = User(
            pseudo = "simple",
            email = "simple@ivanachess.loc",
            creationDate = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC),
            bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
        )

        protected abstract val method: HttpMethod
        protected abstract val path: String

        protected fun buildString(size: Int) = (0 until size).map { 'a' }.fold("") { acc, str -> "$acc$str" }

        protected fun createAuthenticationCookie(jwt: Jwt) = Cookie(props.auth.cookie.name, jwt.token)

        protected fun createEmptyParameterDto(parameter: String) = ErrorDto.InvalidParameter(
            parameter = parameter,
            reason = "must not be empty"
        )

        protected fun createMalformedParameterDto(parameter: String, regex: String) = ErrorDto.InvalidParameter(
            parameter = parameter,
            reason = "must match \"$regex\""
        )

        protected fun createMissingParameterDto(parameter: String) = ErrorDto.InvalidParameter(
            parameter = parameter,
            reason = "must not be null"
        )

        protected fun createInvalidEmailParameterDto(parameter: String) = ErrorDto.InvalidParameter(
            parameter = parameter,
            reason = "must be a well-formed email address"
        )

        protected fun createInvalidSizeParameterDto(parameter: String, min: Int, max: Int) = ErrorDto.InvalidParameter(
            parameter = parameter,
            reason = "size must be between $min and $max"
        )

        protected fun createTooLowParameterDto(parameter: String, min: Int) = ErrorDto.InvalidParameter(
            parameter = parameter,
            reason = "must be greater than or equal to $min"
        )

        protected fun doRequest(
            params: Map<String, List<String>> = emptyMap(),
            cookies: List<Cookie> = emptyList(),
            body: Any? = null,
            expectedStatus: HttpStatus = HttpStatus.OK,
        ) {
            val responseBodyBytes = executeRequest(
                params = params,
                cookies = cookies,
                body = body,
                expectedStatus = expectedStatus,
            )
            responseBodyBytes.isEmpty().shouldBeTrue()
        }

        protected fun <T> doRequest(
            params: Map<String, List<String>> = emptyMap(),
            cookies: List<Cookie> = emptyList(),
            body: Any? = null,
            expectedStatus: HttpStatus = HttpStatus.OK,
            expectedResponseBody: T,
            parseResponseBody: (ByteArray) -> T
        ) {
            val responseBodyBytes = executeRequest(
                params = params,
                cookies = cookies,
                body = body,
                expectedStatus = expectedStatus,
            )
            val responseBody = parseResponseBody(responseBodyBytes)
            responseBody shouldBe expectedResponseBody
        }

        protected fun executeRequest(
            params: Map<String, List<String>> = emptyMap(),
            cookies: List<Cookie> = emptyList(),
            body: Any? = null,
            expectedStatus: HttpStatus = HttpStatus.OK,
        ): ByteArray = mvc.request(method, path) {
            params.forEach { (name, values) -> param(name, *values.toTypedArray()) }
            if (cookies.isNotEmpty()) {
                cookie(*cookies.toTypedArray())
            }
            if (body != null) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(body)
            }
        }
            .andDo { print() }
            .andExpect { status { isEqualTo(expectedStatus.value()) } }
            .andReturn()
            .response
            .contentAsByteArray

        protected fun shouldReturnValidationErrorDto(
            params: Map<String, List<String>> = emptyMap(),
            cookies: List<Cookie> = emptyList(),
            body: Any? = null,
            expectedResponseBody: ErrorDto.Validation
        ) {
            doRequest(
                params = params,
                cookies = cookies,
                body = body,
                expectedStatus = HttpStatus.BAD_REQUEST,
                expectedResponseBody = expectedResponseBody,
            ) { mapper.readValue(it) }
        }

        protected fun withAuthentication(user: User = simpleUser, block: (Jwt) -> Unit) {
            val jwt = Jwt(
                pseudo = user.pseudo,
                expirationDate = OffsetDateTime.now().plusDays(1),
                token = "token"
            )
            whenever(authService.parseJwt(jwt.token)).thenReturn(jwt)
            whenever(authService.loadUserByUsername(user.pseudo)).thenReturn(UserDetailsAdapter(user))
            block(jwt)
            verify(authService, atLeast(1)).parseJwt(jwt.token)
            verify(authService, atLeast(1)).loadUserByUsername(user.pseudo)
        }
    }

    abstract inner class PaginatedEndpointTest : EndpointTest() {
        @Test
        abstract fun `should return validation_error if page parameters are negative`()

        @Test
        abstract fun `should return validation_error if page parameters are 0`()

        protected fun <T> doRequest(
            pageOpts: PageOptions<*>,
            params: Map<String, List<String>> = emptyMap(),
            cookies: List<Cookie> = emptyList(),
            expectedStatus: HttpStatus = HttpStatus.OK,
            expectedResponseBody: PageDto<T>,
            parseResponseBody: (ByteArray) -> PageDto<T>,
        ) {
            val sorts = pageOpts.sorts
                .map { sort ->
                    if (sort.order == EntitySort.Order.Ascending) {
                        sort.field.label
                    } else {
                        "-${sort.field.label}"
                    }
                }
            doRequest(
                params = params + mapOf(
                    ApiConstants.QueryParams.Page to listOf("${pageOpts.number}"),
                    ApiConstants.QueryParams.PageSize to listOf("${pageOpts.size}"),
                    ApiConstants.QueryParams.Sort to sorts,
                ),
                cookies = cookies,
                expectedStatus = expectedStatus,
                expectedResponseBody = expectedResponseBody,
                parseResponseBody = parseResponseBody,
            )
        }

        protected fun shouldReturnValidationErrorDtoIfPageParametersAreInvalid(
            value: Int,
            params: Map<String, List<String>> = emptyMap()
        ) {
            shouldReturnValidationErrorDto(
                params = params + mapOf(
                    ApiConstants.QueryParams.Page to listOf("$value"),
                    ApiConstants.QueryParams.PageSize to listOf("$value"),
                ),
                expectedResponseBody = ErrorDto.Validation(
                    errors = setOf(
                        createTooLowParameterDto(ApiConstants.QueryParams.Page, ApiConstants.Constraints.MinPage),
                        createTooLowParameterDto(
                            parameter = ApiConstants.QueryParams.PageSize,
                            min = ApiConstants.Constraints.MinPageSize
                        ),
                    )
                )
            )
        }
    }

    abstract inner class Paginated {
        @Test
        fun `should return validation_error if page params are negative`() {
            val responseBody = mvc.request(method, path) {
                param(ApiConstants.QueryParams.Page, "-1")
                param(ApiConstants.QueryParams.PageSize, "-1")
            }
                .andDo { print() }
                .andExpect { status { isBadRequest() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Validation>(responseBody) shouldBe ErrorDto.Validation(
                errors = setOf(
                    tooLowNumberInvalidParameter(ApiConstants.QueryParams.Page, 1),
                    tooLowNumberInvalidParameter(ApiConstants.QueryParams.PageSize, 1)
                )
            )
        }

        protected abstract val method: HttpMethod
        protected abstract val path: String
    }

    protected fun missingParameterInvalidParameter(parameter: String) = ErrorDto.InvalidParameter(
        parameter = parameter,
        reason = "must not be null"
    )

    protected fun stringSizeInvalidParameter(parameter: String, min: Int, max: Int) = ErrorDto.InvalidParameter(
        parameter = parameter,
        reason = "size must be between $min and $max"
    )

    protected fun tooHighNumberInvalidParameter(parameter: String, max: Int) = ErrorDto.InvalidParameter(
        parameter = parameter,
        reason = "must be less than or equal to $max"
    )

    protected fun tooLowNumberInvalidParameter(parameter: String, min: Int) = ErrorDto.InvalidParameter(
        parameter = parameter,
        reason = "must be greater than or equal to $min"
    )

    protected fun withAuthentication(user: User = simpleUser, invocationsNb: Int = 1, block: (Jwt) -> Unit) {
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
        cookie(Cookie(props.auth.cookie.name, jwt.token))
    }

    protected fun MockHttpServletRequestDsl.authenticationHeader(jwt: Jwt) {
        header(props.auth.header.name, "${props.auth.header.valuePrefix}${jwt.token}")
    }
}
