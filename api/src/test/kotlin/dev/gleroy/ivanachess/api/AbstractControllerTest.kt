package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.atLeast
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.gleroy.ivanachess.api.security.AuthenticationService
import dev.gleroy.ivanachess.api.security.Jwt
import dev.gleroy.ivanachess.api.security.UserDetailsAdapter
import dev.gleroy.ivanachess.core.*
import dev.gleroy.ivanachess.io.*
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request
import java.time.Clock
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
    protected lateinit var gameService: GameService

    @MockBean
    protected lateinit var matchmaking: Matchmaking

    @MockBean
    protected lateinit var webSocketSender: WebSocketSender

    @MockBean
    protected lateinit var passwordEncoder: BCryptPasswordEncoder

    @MockBean
    protected lateinit var clock: Clock

    @Autowired
    protected lateinit var userConverter: UserConverter

    @Autowired
    protected lateinit var moveConverter: MoveConverter

    @Autowired
    protected lateinit var gameConverter: GameConverter

    @Autowired
    protected lateinit var matchConverter: MatchConverter

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

        protected fun createEmptyParameterErrorRepresentation(parameter: String) = ErrorRepresentation.InvalidParameter(
            parameter = parameter,
            reason = "must not be empty"
        )

        protected fun createMalformedParameterErrorRepresentation(parameter: String, regex: String) =
            ErrorRepresentation.InvalidParameter(
                parameter = parameter,
                reason = "must match \"$regex\""
            )

        protected fun createMissingParameterErrorRepresentation(parameter: String) =
            ErrorRepresentation.InvalidParameter(
                parameter = parameter,
                reason = "must not be null"
            )

        protected fun createInvalidEmailParameterErrorRepresentation(parameter: String) =
            ErrorRepresentation.InvalidParameter(
                parameter = parameter,
                reason = "must be a well-formed email address"
            )

        protected fun createInvalidSizeParameterErrorRepresentation(
            parameter: String,
            min: Int,
            max: Int = Int.MAX_VALUE
        ) = ErrorRepresentation.InvalidParameter(
            parameter = parameter,
            reason = "size must be between $min and $max"
        )

        protected fun createTooHighParameterErrorRepresentation(parameter: String, max: Int) =
            ErrorRepresentation.InvalidParameter(
                parameter = parameter,
                reason = "must be less than or equal to $max"
            )

        protected fun createTooLowParameterErrorRepresentation(parameter: String, min: Int) =
            ErrorRepresentation.InvalidParameter(
                parameter = parameter,
                reason = "must be greater than or equal to $min"
            )

        protected fun doRequest(
            params: Map<String, List<String>> = emptyMap(),
            cookies: List<Cookie> = emptyList(),
            body: Any? = null,
            expectedStatus: HttpStatus = HttpStatus.OK,
        ): MockHttpServletResponse {
            val response = executeRequest(
                params = params,
                cookies = cookies,
                body = body,
                expectedStatus = expectedStatus,
            )
            response.contentAsByteArray.isEmpty().shouldBeTrue()
            return response
        }

        protected fun <T> doRequest(
            params: Map<String, List<String>> = emptyMap(),
            cookies: List<Cookie> = emptyList(),
            body: Any? = null,
            expectedStatus: HttpStatus = HttpStatus.OK,
            expectedResponseBody: T,
            parseResponseBody: (ByteArray) -> T
        ): MockHttpServletResponse {
            val response = executeRequest(
                params = params,
                cookies = cookies,
                body = body,
                expectedStatus = expectedStatus,
            )
            val responseBody = parseResponseBody(response.contentAsByteArray)
            responseBody shouldBe expectedResponseBody
            return response
        }

        protected fun executeRequest(
            params: Map<String, List<String>> = emptyMap(),
            cookies: List<Cookie> = emptyList(),
            body: Any? = null,
            expectedStatus: HttpStatus = HttpStatus.OK,
        ): MockHttpServletResponse = mvc.request(method, path) {
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

        protected fun shouldReturnEntityNotFoundErrorRepresentation(
            params: Map<String, List<String>> = emptyMap(),
            cookies: List<Cookie> = emptyList(),
            body: Any? = null,
        ) {
            doRequest(
                params = params,
                cookies = cookies,
                body = body,
                expectedStatus = HttpStatus.NOT_FOUND,
                expectedResponseBody = ErrorRepresentation.EntityNotFound,
            ) { mapper.readValue(it) }
        }

        protected fun shouldReturnForbiddenErrorRepresentation(
            params: Map<String, List<String>> = emptyMap(),
            cookies: List<Cookie> = emptyList(),
            body: Any? = null,
        ) {
            doRequest(
                params = params,
                cookies = cookies,
                body = body,
                expectedStatus = HttpStatus.FORBIDDEN,
                expectedResponseBody = ErrorRepresentation.Forbidden,
            ) { mapper.readValue(it) }
        }

        protected fun shouldReturnUnauthorized() {
            doRequest(
                expectedStatus = HttpStatus.UNAUTHORIZED,
                expectedResponseBody = ErrorRepresentation.Unauthorized,
            ) { mapper.readValue(it) }
        }

        protected fun shouldReturnValidationErrorRepresentation(
            params: Map<String, List<String>> = emptyMap(),
            cookies: List<Cookie> = emptyList(),
            body: Any? = null,
            expectedResponseBody: ErrorRepresentation.Validation
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
        open fun `should return validation_error if page parameters are negative`() {
            shouldReturnValidationErrorRepresentationIfPageParametersAreInvalid(-1)
        }

        @Test
        open fun `should return validation_error if page parameters are 0`() {
            shouldReturnValidationErrorRepresentationIfPageParametersAreInvalid(0)
        }

        protected fun <T : Representation> doRequest(
            pageOpts: PageOptions,
            params: Map<String, List<String>> = emptyMap(),
            cookies: List<Cookie> = emptyList(),
            expectedStatus: HttpStatus = HttpStatus.OK,
            expectedResponseBody: PageRepresentation<T>,
            parseResponseBody: (ByteArray) -> PageRepresentation<T>,
        ) {
            val sorts = pageOpts.sorts
                .map { sort ->
                    if (sort.order == ItemSort.Order.Ascending) {
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

        protected fun shouldReturnValidationErrorRepresentationIfPageParametersAreInvalid(
            value: Int,
            params: Map<String, List<String>> = emptyMap()
        ) {
            shouldReturnValidationErrorRepresentation(
                params = params + mapOf(
                    ApiConstants.QueryParams.Page to listOf("$value"),
                    ApiConstants.QueryParams.PageSize to listOf("$value"),
                ),
                expectedResponseBody = ErrorRepresentation.Validation(
                    errors = setOf(
                        createTooLowParameterErrorRepresentation(
                            ApiConstants.QueryParams.Page,
                            ApiConstants.Constraints.MinPage
                        ),
                        createTooLowParameterErrorRepresentation(
                            parameter = ApiConstants.QueryParams.PageSize,
                            min = ApiConstants.Constraints.MinPageSize
                        ),
                    )
                )
            )
        }
    }
}
